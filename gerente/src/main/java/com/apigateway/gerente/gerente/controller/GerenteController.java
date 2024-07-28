package com.apigateway.gerente.gerente.controller;
import com.apigateway.gerente.gerente.dto.*;
import com.apigateway.gerente.gerente.model.Gerente;
import com.apigateway.gerente.gerente.repositories.GerenteRepository;
import com.apigateway.gerente.gerente.services.MessagingService;
import com.apigateway.gerente.gerente.utils.Response;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/gerente")
@Log4j2
@CrossOrigin
@Tag(
        name = "API: Gerente",
        description = "Contém todos os endpoints relacionados ao gerente"
)
public class GerenteController {
    @Autowired
    private GerenteRepository repo;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private MessagingService messagingService;
    @Autowired
    private Gson gson; // Reutilizando a instância Gson
    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/listar")
    @Operation(summary = "Lista todos os gerentes")
    public ResponseEntity<Object> listarTodos() {
        try {
            List<Gerente> gerentes = repo.findAll(Sort.by(Sort.Direction.ASC, "nome"));
            return new ResponseEntity<>(new Response(true, "Lista de gerentes recuperada com sucesso", gerentes), HttpStatus.OK);
        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, mensagemErro, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/listar/{id}")
    @Operation(summary = "Busca um gerente pelo ID")
    public ResponseEntity<Object> buscarPorId(@PathVariable Long id) {
        try {
            Gerente gerente = repo.findById(id).orElse(null);
            if (gerente == null) {
                return new ResponseEntity<>(new Response(false, "Gerente não encontrado", null), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(new Response(true, "Gerente encontrado", gerente), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new Response(false, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/listar/email/{email}")
    @Operation(summary = "Busca um gerente pelo e-mail")
    public ResponseEntity<Object> buscarPorEmail(@PathVariable String email) {
        try {
            Gerente gerente = repo.findByEmail(email);
            if (gerente == null) {
                return new ResponseEntity<>(new Response(false, "Gerente não encontrado", null), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(new Response(true, "Gerente encontrado", gerente), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new Response(false, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/pendente-aprovacao/{id}")
    @Operation(summary = "Listagem de cliente pendentes de aprovacao")
    public ResponseEntity<Object> pendentesAprovacao(@PathVariable Long id) {
        try {
            Gerente gerente = repo.findById(id).orElse(null);
            System.out.println(gerente);
            String contasJson = (String) messagingService.sendAndReceiveMessage("conta.get.info.gerente", gerente.getId());
            List<ContaDTO> contas = objectMapper.readValue(contasJson, new TypeReference<List<ContaDTO>>() {});

            System.out.println(contas);
            List<ClienteRelatorioDTO> contasClientes = contas.stream().map(conta -> {

                if(conta.getAprovada() != null){
                    return null;
                }

                ClienteRelatorioDTO clienteRelatorioDTO = new ClienteRelatorioDTO();
                String jsonCliente = (String) messagingService.sendAndReceiveMessage("client.get.info", conta.getIdCliente());
                ClienteDTO cliente = gson.fromJson(jsonCliente, ClienteDTO.class);

                if (cliente != null) {
                    clienteRelatorioDTO.setId(cliente.getId());
                    clienteRelatorioDTO.setNome(cliente.getNome());
                    clienteRelatorioDTO.setEmail(cliente.getEmail());
                    clienteRelatorioDTO.setSalario(cliente.getSalario());
                    clienteRelatorioDTO.setCpf(cliente.getCpf());
                    clienteRelatorioDTO.setTelefone(cliente.getTelefone());
                    clienteRelatorioDTO.setConta(conta);

                    return clienteRelatorioDTO;
                } else {
                    return null;
                }
            }).filter(clienteRelatorioDTO -> clienteRelatorioDTO != null).collect(Collectors.toList());
            return new ResponseEntity<>(new Response(true, "Contas a serem aprovadas", contasClientes), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new Response(false, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/clientes/{idGerente}")
    @Operation(summary = "Listagem de todos os clientes")
    public ResponseEntity<Object> todosClientes(@PathVariable Long idGerente,
                                                @RequestParam(required = false) String cpf,
                                                @RequestParam(required = false) String nome) {
        try {
            Gerente gerente = repo.findById(idGerente).orElse(null);
            if (gerente == null) {
                return new ResponseEntity<>(new Response(false, "Gerente não encontrado", null), HttpStatus.NOT_FOUND);
            }
            System.out.println(gerente);

            String contasJson = (String) messagingService.sendAndReceiveMessage("conta.get.info.gerente", gerente.getId());
            List<ContaDTO> contas = objectMapper.readValue(contasJson, new TypeReference<List<ContaDTO>>() {});

            System.out.println(contas);

            List<ClienteRelatorioDTO> contasClientes = contas.stream().map(conta -> {
                ClienteRelatorioDTO clienteRelatorioDTO = new ClienteRelatorioDTO();
                String jsonCliente = (String) messagingService.sendAndReceiveMessage("client.get.info", conta.getIdCliente());
                ClienteDTO cliente = gson.fromJson(jsonCliente, ClienteDTO.class);


                //Consultando saldo da conta
                String jsonSaldo = (String) messagingService.sendAndReceiveMessage("conta.get.saldo", conta.getNumeroConta());
                SaldoLimiteDTO saldoLimiteDTO = gson.fromJson(jsonSaldo, SaldoLimiteDTO.class);
                conta.setSaldo(saldoLimiteDTO);
                if (cliente != null) {
                    clienteRelatorioDTO.setId(cliente.getId());
                    clienteRelatorioDTO.setNome(cliente.getNome());
                    clienteRelatorioDTO.setEmail(cliente.getEmail());
                    clienteRelatorioDTO.setSalario(cliente.getSalario());
                    clienteRelatorioDTO.setCpf(cliente.getCpf());
                    clienteRelatorioDTO.setTelefone(cliente.getTelefone());
                    clienteRelatorioDTO.setEndereco(cliente.getEndereco());
                    clienteRelatorioDTO.setConta(conta);

                    return clienteRelatorioDTO;
                } else {
                    return null;
                }
            }).filter(clienteRelatorioDTO -> clienteRelatorioDTO != null)
            .filter(clienteRelatorioDTO -> (cpf == null || clienteRelatorioDTO.getCpf().contains(cpf)) && (nome == null || clienteRelatorioDTO.getNome().toLowerCase().contains(nome.toLowerCase())))
            .sorted((c1, c2) -> c1.getNome().compareToIgnoreCase(c2.getNome()))
            .collect(Collectors.toList());

            return new ResponseEntity<>(new Response(true, "Clientes encontrados", contasClientes), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new Response(false, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/dashboard")
    @Operation(summary = "Listagem de todos os gerentes com métricas de clientes e saldos")
    public ResponseEntity<Object> todosGerentesDashboard() {
        try {
            List<Gerente> gerentes = repo.findAll();
            List<GerenteDashboardDTO> gerentesDashboard = new ArrayList<>();

            for (Gerente gerente : gerentes) {
                GerenteDashboardDTO gerenteDashboardDTO = new GerenteDashboardDTO();
                gerenteDashboardDTO.setId(gerente.getId());
                gerenteDashboardDTO.setNome(gerente.getNome());

                String contasJson = (String) messagingService.sendAndReceiveMessage("conta.get.info.gerente", gerente.getId());
                List<ContaDTO> contas = objectMapper.readValue(contasJson, new TypeReference<List<ContaDTO>>() {});

                double saldoPositivoTotal = 0.0;
                double saldoNegativoTotal = 0.0;

                for (ContaDTO conta : contas) {
                    // Consultando saldo da conta
                    String jsonSaldo = (String) messagingService.sendAndReceiveMessage("conta.get.saldo", conta.getNumeroConta());
                    SaldoLimiteDTO saldoLimiteDTO = gson.fromJson(jsonSaldo, SaldoLimiteDTO.class);
                    conta.setSaldo(saldoLimiteDTO);

                    if (saldoLimiteDTO.getSaldo() >= 0) {
                        saldoPositivoTotal += saldoLimiteDTO.getSaldo();
                    } else {
                        saldoNegativoTotal += saldoLimiteDTO.getSaldo();
                    }
                }

                gerenteDashboardDTO.setNumeroClientes(contas.size());
                gerenteDashboardDTO.setSaldoPositivoTotal(saldoPositivoTotal);
                gerenteDashboardDTO.setSaldoNegativoTotal(saldoNegativoTotal);
                gerentesDashboard.add(gerenteDashboardDTO);
            }

            return new ResponseEntity<>(new Response(true, "Gerentes encontrados", gerentesDashboard), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new Response(false, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}