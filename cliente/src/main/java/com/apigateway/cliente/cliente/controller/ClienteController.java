package com.apigateway.cliente.cliente.controller;
import com.apigateway.cliente.cliente.dto.ClienteDTO;
import com.apigateway.cliente.cliente.dto.EnderecoDTO;
import com.apigateway.cliente.cliente.repositories.ClienteRepository;
import com.apigateway.cliente.cliente.repositories.EnderecoRepository;
import com.apigateway.cliente.cliente.services.MessagingService;
import com.apigateway.cliente.cliente.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;
import com.apigateway.cliente.cliente.model.Cliente;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

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

    @PostMapping("adicionar")
    @Operation(
            summary = "Endpoint para adicionar cliente",
            description = "Retorna o último cliente adicionado"
    )
    public ResponseEntity<Object> inserir(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request ilustrativa") @RequestBody ClienteDTO clienteDTO)
    {
        try {
            if (clienteDTO.getNome() == null || clienteDTO.getCpf() == null) {
                return new ResponseEntity<>(new Response(false, "Dados do cliente inválidos", null), HttpStatus.BAD_REQUEST);
            }

            EnderecoDTO endereco = clienteDTO.getEndereco();
            if (endereco.getLogradouro() == null || endereco.getNumero() == null || endereco.getCidade() == null || endereco.getUf() == null || endereco.getCep() == null) {
                return new ResponseEntity<>(new Response(false, "Dados do endereço inválidos", null), HttpStatus.BAD_REQUEST);
            }
            Cliente clienteObj = repo.saveAndFlush(mapper.map(clienteDTO, Cliente.class));
            this.messagingService.sendMessage("cliente.created", clienteObj);
            return new ResponseEntity<>(new Response(true, "Cliente criado com sucesso", clienteObj), HttpStatus.OK);
        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, mensagemErro, null), HttpStatus.BAD_REQUEST);
        }
    }
}