package com.apigateway.cliente.cliente.controller;
import com.apigateway.cliente.cliente.dto.*;
import com.apigateway.cliente.cliente.repositories.ClienteRepository;
import com.apigateway.cliente.cliente.repositories.EnderecoRepository;
import com.apigateway.cliente.cliente.services.MessagingService;
import com.apigateway.cliente.cliente.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.log4j.Log4j2;
import com.apigateway.cliente.cliente.model.Cliente;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.google.gson.Gson;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/api/cliente")
@Log4j2
@Tag(
        name = "API: Clientes",
        description = "Contém todos os endpoints relacionados ao cliente"
)
public class ClienteController {
    @Autowired
    private ClienteRepository repo;
    @Autowired
    private EnderecoRepository repoEnd;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private MessagingService messagingService;
    @Autowired
    private Gson gson; // Reutilizando a instância Gson

    @GetMapping("listar")
    @Operation(
            summary = "Endpoint para listagem de clientes",
            description = "Retorna uma lista com todos os clientes"
    )
    public ResponseEntity<Object> listAll() {
        try {
            List<Cliente> clientes = repo.findAll();
            return new ResponseEntity<>(new Response(true, "Lista de clientes retornada com sucesso", clientes), HttpStatus.OK);
        }catch (Exception e) {
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, mensagemErro, null), HttpStatus.BAD_REQUEST);
        }
    }

//    @PostMapping("adicionar")
//    @Operation(
//            summary = "Endpoint para adicionar cliente",
//            description = "Retorna o último cliente adicionado"
//    )
//    @ApiResponse(responseCode = "403", description = "CPF ou E-mail duplicado", content = @Content(schema = @Schema(implementation = Response.class)))
//    public ResponseEntity<Object> inserir(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request ilustrativa") @RequestBody ClienteDTO clienteDTO)
//    {
//        try {
//            if (clienteDTO.getNome() == null || clienteDTO.getCpf() == null) {
//                return new ResponseEntity<>(new Response(false, "Dados do cliente inválidos", null), HttpStatus.BAD_REQUEST);
//            }
//
//            EnderecoDTO endereco = clienteDTO.getEndereco();
//            if (endereco.getLogradouro() == null || endereco.getNumero() == null || endereco.getCidade() == null || endereco.getUf() == null || endereco.getCep() == null) {
//                return new ResponseEntity<>(new Response(false, "Dados do endereço inválidos", null), HttpStatus.BAD_REQUEST);
//            }
//            Cliente clienteObj = repo.saveAndFlush(mapper.map(clienteDTO, Cliente.class));
//            this.messagingService.sendMessage("cliente.created", clienteObj);
//            return new ResponseEntity<>(new Response(true, "Cliente criado com sucesso", clienteObj), HttpStatus.OK);
//        } catch (DataIntegrityViolationException e) {
//            String mensagemErro = "Um registro com o mesmo CPF ou e-mail já existe.";
//            return new ResponseEntity<>(new Response(false, mensagemErro, null), HttpStatus.CONFLICT);
//        } catch (Exception e) {
//            String mensagemErro = e.getMessage();
//            return new ResponseEntity<>(new Response(false, mensagemErro, null), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }


    @GetMapping("/cpf/{cpf}")
    @Operation(
            summary = "Busca um cliente pelo cpf",
            description = "Retorna o relatório de clientes R16"
    )
    public ResponseEntity<Object> getClienteByCpf(@PathVariable(value = "cpf") String cpf){
        try {
            Cliente cliente = repo.findByCpf(cpf);
            ClienteRelatorioDTO clienteRelatorioDTO = new ClienteRelatorioDTO();
            if (cliente == null) {
                return new ResponseEntity<>(new Response(false, "Cliente não encontrado", null), HttpStatus.NOT_FOUND);
            }

            String jsonConta = (String) messagingService.sendAndReceiveMessage("conta.get.info", cliente.getId());
            ContaDTO contaDTO = gson.fromJson(jsonConta, ContaDTO.class);

            //Consultando saldo da conta
            String jsonSaldo = (String) messagingService.sendAndReceiveMessage("conta.get.saldo", contaDTO.getNumeroConta());
            SaldoLimiteDTO saldoLimiteDTO = gson.fromJson(jsonSaldo, SaldoLimiteDTO.class);
            contaDTO.setSaldo(saldoLimiteDTO);

            clienteRelatorioDTO.setConta(contaDTO);

            //dados do cliente
            clienteRelatorioDTO.setId(cliente.getId());
            clienteRelatorioDTO.setNome(cliente.getNome());
            clienteRelatorioDTO.setEmail(cliente.getEmail());
            clienteRelatorioDTO.setSalario(cliente.getSalario());
            clienteRelatorioDTO.setCpf(cliente.getCpf());
            clienteRelatorioDTO.setTelefone(cliente.getTelefone());

            EnderecoDTO enderecoDTO = new EnderecoDTO();
            enderecoDTO.setId(cliente.getEndereco().getId());
            enderecoDTO.setTipo(cliente.getEndereco().getTipo());
            enderecoDTO.setLogradouro(cliente.getEndereco().getLogradouro());
            enderecoDTO.setNumero(cliente.getEndereco().getNumero());
            enderecoDTO.setComplemento(cliente.getEndereco().getComplemento());
            enderecoDTO.setCep(cliente.getEndereco().getCep());
            enderecoDTO.setCidade(cliente.getEndereco().getCidade());
            enderecoDTO.setUf(cliente.getEndereco().getUf());
            clienteRelatorioDTO.setEndereco(enderecoDTO);

            return new ResponseEntity<>(new Response(true, "Cliente encontrado", clienteRelatorioDTO), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new Response(false, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("relatorio")
    @Operation(
            summary = "Endpoint para relatório de clientes",
            description = "Retorna o relatório de clientes R16"
    )
    public ResponseEntity<Object> relatorioClientes() {
        try {
            List<Cliente> clientes = repo.findAll();
            List<ClienteRelatorioDTO> contasClientes = clientes.stream().map(cliente -> {
                String jsonConta = (String) messagingService.sendAndReceiveMessage("conta.get.info", cliente.getId());
                ContaDTO conta = gson.fromJson(jsonConta, ContaDTO.class);
                ClienteRelatorioDTO clienteDTO = new ClienteRelatorioDTO();

                if (conta != null && conta.getIdGerente() != null) {
                    try {
                        String jsonGerente = (String) messagingService.sendAndReceiveMessage("gerente.get.info", conta.getIdGerente());
                        GerenteDTO gerente = gson.fromJson(jsonGerente, GerenteDTO.class);
                        clienteDTO.setGerente(gerente);

                        String jsonSaldo = (String) messagingService.sendAndReceiveMessage("conta.get.saldo", conta.getNumeroConta());
                        SaldoLimiteDTO saldoLimiteDTO = gson.fromJson(jsonSaldo, SaldoLimiteDTO.class);

                        conta.setSaldo(saldoLimiteDTO);
                    } catch (Exception e) {
                        System.err.println("Erro ao obter informações do gerente: " + e.getMessage());
                    }
                }

                clienteDTO.setId(cliente.getId());
                clienteDTO.setNome(cliente.getNome());
                clienteDTO.setEmail(cliente.getEmail());
                clienteDTO.setSalario(cliente.getSalario());
                clienteDTO.setCpf(cliente.getCpf());
                clienteDTO.setTelefone(cliente.getTelefone());

                if(conta != null){
                    clienteDTO.setConta(conta);
                }

                return clienteDTO;
            }).sorted(Comparator.comparing(ClienteRelatorioDTO::getNome)).collect(Collectors.toList());

            return new ResponseEntity<>(new Response(true, "Relatório de clientes retornada com sucesso", contasClientes), HttpStatus.OK);
        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, mensagemErro, null), HttpStatus.BAD_REQUEST);
        }
    }
}