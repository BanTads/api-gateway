package com.apigateway.conta.conta.helpers;

import com.apigateway.conta.conta.constants.QueueConstants;
import com.apigateway.conta.conta.dto.ContaDTO;
import com.apigateway.conta.conta.dto.GerenteReassignmentDTO;
import com.apigateway.conta.conta.dto.SaldoLimiteDTO;
import com.apigateway.conta.conta.model.Conta;
import com.apigateway.conta.conta.repositories.ContaRepository;
import com.apigateway.conta.conta.repositories.MovimentacaoRepository;
import com.apigateway.conta.conta.utils.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Random;

@Component
public class ContaHelper {
    @Autowired
    private ContaRepository repo;
    @Autowired
    private MovimentacaoRepository repoMovimentacao;
    @Autowired
    private ModelMapper mapper;
    public ResponseEntity<Object> saveAccount(ContaDTO contaDTO) {
        try {
            contaDTO.setAprovada(null);
            contaDTO.setDataCriacao(new Date());
            Conta contaCriada = repo.saveAndFlush(mapper.map(contaDTO, Conta.class));
            return new ResponseEntity<>(new Response(true, "Conta criada com sucesso", contaCriada,  HttpStatus.OK.value()), HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            String mensagemErro = "Conta duplicada";
            return new ResponseEntity<>(new Response(false, mensagemErro, null, HttpStatus.CONFLICT.value()), HttpStatus.CONFLICT);
        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, mensagemErro, null, HttpStatus.CONFLICT.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> updateLimit(ContaDTO contaDTO) {
        try {
            Conta contaExistente = repo.findById(contaDTO.getNumeroConta()).orElse(null);
            contaDTO.setLimite(contaDTO.getLimite());
            Conta contaCriada = repo.saveAndFlush(mapper.map(contaDTO, Conta.class));
            return new ResponseEntity<>(new Response(true, "Limite da conta atualizado com sucesso", contaCriada,  HttpStatus.OK.value()), HttpStatus.OK);
        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, mensagemErro, null, HttpStatus.CONFLICT.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> reassignAccount(GerenteReassignmentDTO reassignmentDTO) {
        try {
            List<Conta> contas = repo.findByIdGerente(reassignmentDTO.getOldGerenteId());
            if (reassignmentDTO.getGerenteCriado()) {
                // Atribuir apenas uma conta aleatória ao novo gerente
                if (!contas.isEmpty()) {
                    Random random = new Random();
                    Conta contaSelecionada = contas.get(random.nextInt(contas.size()));
                    contaSelecionada.setIdGerente(reassignmentDTO.getNewGerenteId());
                    repo.save(contaSelecionada);
                }
            } else {
                // Atribuir todas as contas ao novo gerente
                for (Conta conta : contas) {
                    conta.setIdGerente(reassignmentDTO.getNewGerenteId());
                    repo.save(conta);
                }
            }
            return new ResponseEntity<>(new Response(true, "Manejamento de contas feito com sucesso", null,  HttpStatus.OK.value()), HttpStatus.OK);
        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, mensagemErro, null, HttpStatus.CONFLICT.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public SaldoLimiteDTO calcularSaldoELimite(Long numeroConta) {
        Conta conta = repo.findById(numeroConta).orElse(null);
        if (conta == null) {
            throw new RuntimeException("Conta não encontrada");
        }

        // Calcular o saldo
        Double saldo = repoMovimentacao.calcularSaldo(numeroConta);
        saldo = (saldo != null) ? saldo : 0.00;

        // Recuperar o limite da conta
        Double limite = conta.getLimite();

        // Calcular o total
        Double total = saldo + limite;

        // Retornar objeto DTO
        return new SaldoLimiteDTO(saldo, limite, total);
    }
}
