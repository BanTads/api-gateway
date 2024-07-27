package com.apigateway.conta.conta.controller;

import com.apigateway.conta.conta.dto.*;
import com.apigateway.conta.conta.helpers.ContaHelper;
import com.apigateway.conta.conta.model.Conta;
import com.apigateway.conta.conta.model.Movimentacao;
import com.apigateway.conta.conta.repositories.ContaRepository;
import com.apigateway.conta.conta.repositories.MovimentacaoRepository;
import com.apigateway.conta.conta.services.MessagingService;
import com.apigateway.conta.conta.utils.EmailService;
import com.apigateway.conta.conta.utils.Response;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/api/conta")
@Log4j2
@Tag(
        name = "API: Conta",
        description = "Contém todos os endpoints relacionados a conta"
)
public class ContaController {
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private ContaRepository repo;
    @Autowired
    private MovimentacaoRepository repoMovimentacao;
    @Autowired
    private MessagingService messagingService;
    @Autowired
    private ContaHelper contaHelper;
    @Autowired
    private EmailService emailService;
    @Autowired
    private Gson gson; // Reutilizando a instância Gson
    @PutMapping("/atualizar/{numeroConta}")
    @Operation(summary = "Atualiza uma conta pelo id")
    public ResponseEntity<Object> atualizar(@PathVariable Long numeroConta, @RequestBody ContaDTO contaDTO) {
        try {
            Conta contaExistente = repo.findById(numeroConta).orElse(null);
            if (contaExistente == null) {
                return new ResponseEntity<>(new Response(false, "Conta não encontrada", null), HttpStatus.NOT_FOUND);
            }

            if (contaExistente.getNumeroConta() != numeroConta) {
                return new ResponseEntity<>(new Response(false, "ID da conta não pode ser alterado", null), HttpStatus.BAD_REQUEST);
            }

            String jsonCliente = (String) messagingService.sendAndReceiveMessage("client.get.info", contaExistente.getIdCliente());
            ClienteDTO cliente = gson.fromJson(jsonCliente, ClienteDTO.class);

            if (contaDTO.getAprovada() != null && contaDTO.getAprovada() && (contaExistente.getAprovada() == null || !contaExistente.getAprovada())) {  //se a conta for aprovada
                UserDTO userDTO = new UserDTO();
                userDTO.setEmail(cliente.getEmail());
                userDTO.setNome(cliente.getNome());
                userDTO.setCargo("CLIENTE");

                Response responseUsuarioCriado = (Response) messagingService.sendAndReceiveMessageObj("user.insert",  userDTO);
                System.out.println(responseUsuarioCriado);
                if (!responseUsuarioCriado.getSuccess() && responseUsuarioCriado.getCode() != 404)
                    return new ResponseEntity<>(new Response(false, "Erro ao criar usuário: " + responseUsuarioCriado.getMessage(), null), HttpStatus.valueOf(responseUsuarioCriado.getCode()));
            }

            if(contaDTO.getAprovada() != null && !contaDTO.getAprovada() && contaExistente.getAprovada() == null){ //se a conta for reprovada
                if(contaDTO.getMotivo() == null){
                    return new ResponseEntity<>(new Response(false, "Motivo não pode ser vazio", null), HttpStatus.BAD_REQUEST);
                }
                emailService.sendEmail(cliente.getEmail(), "Sua conta não foi aprovada", contaDTO.getMotivo(), cliente.getNome());
            }

            contaExistente.setMotivo(contaDTO.getMotivo());
            contaExistente.setAprovada(contaDTO.getAprovada());
            repo.saveAndFlush(contaExistente);
            return new ResponseEntity<>(new Response(true, "Conta atualizada com sucesso", contaExistente), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new Response(false, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/saldo/{numeroConta}")
    @Operation(summary = "Saldo de uma conta")
    public ResponseEntity<Object> saldo(@PathVariable Long numeroConta) {
        try {
            SaldoLimiteDTO saldoLimiteDTO = contaHelper.calcularSaldoELimite(numeroConta);
            return new ResponseEntity<>(new Response(true, "Saldo e limite listados com sucesso", saldoLimiteDTO), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new Response(false, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/transacao")
    @Operation(summary = "Realiza uma transação (SAQUE, DEPOSITO, TRANSFERENCIA)")
    public ResponseEntity<Object> realizarTransacao(@RequestBody MovimentacaoDTO movimentacaoDTO) {
        try {
            TipoMovimentacao tipoMovimentacao;
            try {
                tipoMovimentacao = TipoMovimentacao.valueOf(movimentacaoDTO.getTipo().name());
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(new Response(false, "Tipo de movimentação inválido: " + movimentacaoDTO.getTipo(), null), HttpStatus.BAD_REQUEST);
            }

            // Verificar a conta de origem se a transação não for um depósito
            if (movimentacaoDTO.getTipo() != TipoMovimentacao.DEPOSITO) {
                Conta contaOrigem = repo.findById(movimentacaoDTO.getIdContaOrigem()).orElse(null);
                if (contaOrigem == null) {
                    return new ResponseEntity<>(new Response(false, "Conta de origem não encontrada", null), HttpStatus.NOT_FOUND);
                }

                // Calcular saldo e limite da conta origem
                SaldoLimiteDTO saldoLimiteDTO = contaHelper.calcularSaldoELimite(movimentacaoDTO.getIdContaOrigem());
                Double saldoDisponivel = saldoLimiteDTO.getSaldo() + saldoLimiteDTO.getLimite();

                // Verificar saldo suficiente para saque ou transferência
                if (movimentacaoDTO.getTipo() == TipoMovimentacao.SAQUE || movimentacaoDTO.getTipo() == TipoMovimentacao.TRANSFERENCIA) {
                    if (saldoDisponivel < movimentacaoDTO.getValor()) {
                        return new ResponseEntity<>(new Response(false, "Saldo insuficiente para realizar a transação", saldoLimiteDTO), HttpStatus.BAD_REQUEST);
                    }
                }
            }

            // Verificar a conta de destino se a transação for um depósito ou transferência
            if (movimentacaoDTO.getTipo() == TipoMovimentacao.DEPOSITO || movimentacaoDTO.getTipo() == TipoMovimentacao.TRANSFERENCIA) {
                Conta contaDestino = repo.findById(movimentacaoDTO.getIdContaDestino()).orElse(null);
                if (contaDestino == null) {
                    return new ResponseEntity<>(new Response(false, "Conta de destino não encontrada", null), HttpStatus.NOT_FOUND);
                }
            }

            movimentacaoDTO.setTipo(tipoMovimentacao);
            movimentacaoDTO.setDataHora(new Date());
            Movimentacao movimentacaoCriada = repoMovimentacao.saveAndFlush(mapper.map(movimentacaoDTO, Movimentacao.class));
            return new ResponseEntity<>(new Response(true, "Transação realizada com sucesso", movimentacaoCriada), HttpStatus.OK);
        }  catch (Exception e) {
            return new ResponseEntity<>(new Response(false, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/extrato")
    @Operation(summary = "Consulta de extrato")
    public ResponseEntity<Object> consultarExtrato(@RequestParam Long idConta,
                                                   @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataInicio,
                                                   @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataFim) {
        try {
            ZonedDateTime dataInicioAjustada = dataInicio.atStartOfDay(ZoneId.of("GMT")).plusHours(6);
            ZonedDateTime dataFimAjustada = dataFim.plusDays(1).atStartOfDay(ZoneId.of("GMT")).plusHours(6);
            Date dataInicioDate = Date.from(dataInicioAjustada.toInstant());
            Date dataFimDate = Date.from(dataFimAjustada.toInstant());

            System.out.println(dataInicioDate);
            System.out.println(dataFimDate);
            List<Movimentacao> movimentacoes = repoMovimentacao.findMovimentacoesByDateRangeAndConta(idConta, dataInicioDate, dataFimDate);

            System.out.println(movimentacoes);
            List<ExtratoDTO> extrato = new ArrayList<>();
            Map<LocalDate, Double> saldoConsolidadoPorDia = new HashMap<>();

            Double saldoAtual = 0.0;

            // Inicializa a data para o saldo consolidado diário
            ZonedDateTime dataAtual = dataInicioAjustada.minusDays(1);

            while (!dataAtual.isAfter(dataFimAjustada)) {
                LocalDate diaAtual = dataAtual.toLocalDate();
                ZonedDateTime finalDataAtual = dataAtual;
                List<Movimentacao> movimentacoesDoDia = movimentacoes.stream()
                        .filter(m -> isSameDay(m.getDataHora(), Date.from(finalDataAtual.toInstant())))
                        .collect(Collectors.toList());

                for (Movimentacao m : movimentacoesDoDia) {
                    ExtratoDTO extratoDTO = new ExtratoDTO();
                    extratoDTO.setDataHora(m.getDataHora());
                    extratoDTO.setOperacao(m.getTipo().name());
                    extratoDTO.setValor(m.getValor());


                    if (m.getTipo() == TipoMovimentacao.TRANSFERENCIA) {
                        Long idCliente = null;

                        if (idConta.equals(m.getIdContaOrigem())) {
                            idCliente = repo.findById(m.getIdContaDestino()).map(Conta::getIdCliente).orElse(null);
                        } else if (idConta.equals(m.getIdContaDestino())) {
                            idCliente = repo.findById(m.getIdContaOrigem()).map(Conta::getIdCliente).orElse(null);
                        }

                        String jsonCliente = (String) messagingService.sendAndReceiveMessage("client.get.info", idCliente);
                        ClienteDTO cliente = gson.fromJson(jsonCliente, ClienteDTO.class);
                        extratoDTO.setIdCliente(cliente.getId());
                        extratoDTO.setNomeCliente(cliente.getNome());
                    }


                    if (idConta.equals(m.getIdContaOrigem())) {
                        extratoDTO.setTipo("saida");
                        saldoAtual -= m.getValor();
                    } else if (idConta.equals(m.getIdContaDestino())) {
                        extratoDTO.setTipo("entrada");
                        saldoAtual += m.getValor();
                    }

                    extratoDTO.setSaldoConsolidado(saldoAtual);
                    extrato.add(extratoDTO);
                }

                saldoConsolidadoPorDia.put(diaAtual, saldoAtual);

                // Avança para o próximo dia
                dataAtual = dataAtual.plusDays(1);
            }

            ExtratoResponseDTO response = new ExtratoResponseDTO();
            response.setExtrato(extrato);
            response.setSaldoFinal(saldoAtual);

            return new ResponseEntity<>(new Response(true, "Extrato consultado com sucesso", response), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new Response(false, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/top3/{idGerente}")
    public ResponseEntity<Object> getTop3Clientes(@PathVariable Long idGerente) {
        try {
            List<Conta> contas = repo.findByIdGerente(idGerente);

            // Calcula saldo e total para cada conta
            List<SaldoLimiteDTO> contasComSaldo = contas.stream().map(conta -> {
                Double saldo = repoMovimentacao.calcularSaldo(conta.getNumeroConta());
                saldo = (saldo != null) ? saldo : 0.0;
                return new SaldoLimiteDTO(saldo, conta.getLimite(), saldo + conta.getLimite(), conta);
            }).collect(Collectors.toList());

            // Ordena as contas pelo saldo total e pega as top 3
            List<SaldoLimiteDTO> top3Contas = contasComSaldo.stream()
                    .sorted((c1, c2) -> Double.compare(c2.getTotal(), c1.getTotal())) // Ordenação decrescente
                    .limit(3)
                    .collect(Collectors.toList());

            // Monta o DTO de resposta
            List<ClienteRelatorioDTO> top3Clientes = top3Contas.stream().map(saldoLimiteDTO -> {
                ClienteRelatorioDTO clienteRelatorioDTO = new ClienteRelatorioDTO();

                Conta conta = saldoLimiteDTO.getConta();

                clienteRelatorioDTO.setSaldo(saldoLimiteDTO.getSaldo());
                clienteRelatorioDTO.setLimite(saldoLimiteDTO.getLimite());
                clienteRelatorioDTO.setTotal(saldoLimiteDTO.getTotal());

                // Recupera informações do cliente via mensagem
                String jsonCliente = (String) messagingService.sendAndReceiveMessage("client.get.info", conta.getIdCliente());
                ClienteDTO cliente = gson.fromJson(jsonCliente, ClienteDTO.class);

                if (cliente != null) {
                    clienteRelatorioDTO.setId(cliente.getId());
                    clienteRelatorioDTO.setNome(cliente.getNome());
                    clienteRelatorioDTO.setEmail(cliente.getEmail());
                    clienteRelatorioDTO.setSalario(cliente.getSalario());
                    clienteRelatorioDTO.setCpf(cliente.getCpf());
                    clienteRelatorioDTO.setTelefone(cliente.getTelefone());
                    clienteRelatorioDTO.setEndereco(cliente.getEndereco());
                }

                return clienteRelatorioDTO;
            }).collect(Collectors.toList());

            return new ResponseEntity<>(new Response(true, "Top 3 clientes com maiores saldos", top3Clientes), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new Response(false, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isSameDay(Date date1, Date date2) {
        LocalDate localDate1 = date1.toInstant().atZone(ZoneId.of("UTC+3")).toLocalDate();
        LocalDate localDate2 = date2.toInstant().atZone(ZoneId.of("UTC+3")).toLocalDate();
        return localDate1.equals(localDate2);
    }
}
