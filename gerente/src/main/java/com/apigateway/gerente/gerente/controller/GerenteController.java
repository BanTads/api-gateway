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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/gerente")
@Log4j2
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

    @PostMapping("adicionar")
    @Operation(
            summary = "Endpoint para adicionar gerente",
            description = "Retorna o último gerente adicionado"
    )

    @ApiResponse(responseCode = "403", description = "CPF ou E-mail duplicado", content = @Content(schema = @Schema(implementation = Response.class)))
    public ResponseEntity<Object> inserir(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request ilustrativa") @RequestBody GerenteDTO gerenteDTO)
    {
        try {
            if (gerenteDTO.getNome() == null || gerenteDTO.getEmail() == null || gerenteDTO.getCpf() == null || gerenteDTO.getTelefone() == null) {
                return new ResponseEntity<>(new Response(false, "Dados do gerente inválidos", null), HttpStatus.BAD_REQUEST);
            }

            Gerente gerenteObj = repo.saveAndFlush(mapper.map(gerenteDTO, Gerente.class));

            List<Gerente> outrosGerentes = repo.findAll();
            Gerente gerenteComMaisContas = outrosGerentes.stream()
                    .filter(g -> g.getId() != gerenteObj.getId() && g.getQuantidadeContas() > 1)
                    .max(Comparator.comparingInt(Gerente::getQuantidadeContas))
                    .orElse(null);

            if (gerenteComMaisContas != null) {
                gerenteObj.setQuantidadeContas(1);  // Atribui uma conta ao novo gerente
                gerenteComMaisContas.setQuantidadeContas(gerenteComMaisContas.getQuantidadeContas() - 1);
                repo.save(gerenteComMaisContas);
                repo.save(gerenteObj);

                GerenteReassignmentDTO message = new GerenteReassignmentDTO(gerenteComMaisContas.getId(), gerenteObj.getId(), true);
                messagingService.sendMessage("account.reassign.manager", message);
            }

            messagingService.sendMessage("manager.created", gerenteObj);
            return new ResponseEntity<>(new Response(true, "Gerente criado com sucesso", gerenteObj), HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            String mensagemErro = "Um registro com o mesmo CPF ou e-mail já existe.";
            return new ResponseEntity<>(new Response(false, mensagemErro, null), HttpStatus.CONFLICT);
        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, mensagemErro, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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

    @PutMapping("/atualizar/{id}")
    @ApiResponse(responseCode = "403", description = "CPF ou E-mail duplicado", content = @Content(schema = @Schema(implementation = Response.class)))
    @Operation(summary = "Atualiza um gerente existente pelo ID")
    public ResponseEntity<Object> atualizar(@PathVariable Long id, @RequestBody GerenteDTO gerenteDTO) {
        try {
            Gerente gerenteExistente = repo.findById(id).orElse(null);
            if (gerenteExistente == null) {
                return new ResponseEntity<>(new Response(false, "Gerente não encontrado", null), HttpStatus.NOT_FOUND);
            }

            if (gerenteExistente.getId() != id) {
                return new ResponseEntity<>(new Response(false, "ID do gerente não pode ser alterado", null), HttpStatus.BAD_REQUEST);
            }

            gerenteExistente.setNome(gerenteDTO.getNome());
            gerenteExistente.setEmail(gerenteDTO.getEmail());
            repo.saveAndFlush(gerenteExistente);
            messagingService.sendMessage("manager.edited", gerenteExistente);
            return new ResponseEntity<>(new Response(true, "Gerente atualizado com sucesso", gerenteExistente), HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            String mensagemErro = "Um registro com o mesmo CPF ou e-mail já existe.";
            return new ResponseEntity<>(new Response(false, mensagemErro, null), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(new Response(false, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/remover/{id}")
    @Operation(summary = "Remove um gerente pelo ID")
    public ResponseEntity<Object> remover(@PathVariable Long id) {
        try {
            if (!repo.existsById(id)) {
                return new ResponseEntity<>(new Response(false, "Gerente não encontrado", null), HttpStatus.NOT_FOUND);
            }

            Gerente gerenteToRemove = repo.findById(id).orElse(null);
            if (gerenteToRemove == null) {
                return new ResponseEntity<>(new Response(false, "Gerente não encontrado", null), HttpStatus.NOT_FOUND);
            }

            // Verificar se há mais de um gerente
            if (repo.count() <= 1) {
                return new ResponseEntity<>(new Response(false, "Não é permitido remover o último gerente do banco.", null), HttpStatus.BAD_REQUEST);
            }

            List<Gerente> gerentes = repo.findByIdNot(id);
            Gerente gerenteComMenosContas = gerentes.stream()
                    .min(Comparator.comparingInt(Gerente::getQuantidadeContas))
                    .orElse(null);


            if (gerenteComMenosContas == null) {
                return new ResponseEntity<>(new Response(false, "Não foi possível encontrar um substituto para as contas.", null), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            gerenteComMenosContas.setQuantidadeContas(gerenteComMenosContas.getQuantidadeContas() + gerenteToRemove.getQuantidadeContas());
            repo.save(gerenteComMenosContas);

            GerenteReassignmentDTO message = new GerenteReassignmentDTO(gerenteToRemove.getId(), gerenteComMenosContas.getId(), false);
            messagingService.sendMessage("reassign.manager", message);
            return new ResponseEntity<>(new Response(true, "Gerente removido com sucesso", null), HttpStatus.OK);
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
                return new ResponseEntity<>(new Response(false, "Gerente not found", null), HttpStatus.NOT_FOUND);
            }
            System.out.println(gerente);

            // Fetch accounts associated with the manager
            String contasJson = (String) messagingService.sendAndReceiveMessage("conta.get.info.gerente", gerente.getId());
            List<ContaDTO> contas = objectMapper.readValue(contasJson, new TypeReference<List<ContaDTO>>() {});

            System.out.println(contas);

            // Process each account to fetch client information
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
}