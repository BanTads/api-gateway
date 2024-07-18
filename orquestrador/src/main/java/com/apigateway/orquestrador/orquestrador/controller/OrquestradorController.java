package com.apigateway.orquestrador.orquestrador.controller;

import com.apigateway.orquestrador.orquestrador.constants.QueueConstants;
import com.apigateway.orquestrador.orquestrador.dto.ClienteDTO;
import com.apigateway.orquestrador.orquestrador.dto.ContaDTO;
import com.apigateway.orquestrador.orquestrador.dto.GerenteDTO;
import com.apigateway.orquestrador.orquestrador.services.MessagingService;
import com.apigateway.orquestrador.orquestrador.utils.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api")
@Log4j2
@Tag(
        name = "API: BANTADS",
        description = "Contém todos os endpoints relacionados ao BANTADS"
)

public class OrquestradorController {
    @Autowired
    private MessagingService messagingService;
    @Autowired
    private ObjectMapper objectMapper;
    @PostMapping("autocadastro")
    @Operation(
            summary = "Endpoint para autocadastro de cliente",
            description = "Retorna os dados do cliente adicionado"
    )
    @ApiResponse(responseCode = "403", description = "CPF ou E-mail duplicado", content = @Content(schema = @Schema(implementation = Response.class)))
    public ResponseEntity<Object> inserir(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request ilustrativa") @RequestBody ClienteDTO clienteDTO) {
        try {
            GerenteDTO gerenteDTO = new GerenteDTO();
            //gerente com menos contas
            Response responseManager = (Response) messagingService.sendAndReceiveMessage(QueueConstants.MANAGER_MIN_ACCOUNT, clienteDTO);
            System.out.println("oioi");
            System.out.println(responseManager);
            //retornando erro caso exista algum para cliente
            if (!responseManager.getSuccess()) {
                return new ResponseEntity<>(new Response(false, responseManager.getMessage(), null), HttpStatus.valueOf(responseManager.getCode()));
            }

            if(responseManager.getData() != null){
                gerenteDTO = objectMapper.convertValue(responseManager.getData(), GerenteDTO.class);
            }

            Response response = (Response) messagingService.sendAndReceiveMessage(QueueConstants.CLIENT_INSERT, clienteDTO);
            //retornando erro caso exista algum para gerente
            if (!response.getSuccess()) {
                return new ResponseEntity<>(new Response(false, response.getMessage(), null), HttpStatus.valueOf(response.getCode()));
            }

            if(response.getData() != null){
                clienteDTO = objectMapper.convertValue(response.getData(), ClienteDTO.class);
            }

            ContaDTO contaDTO = new ContaDTO();
            contaDTO.setIdGerente(gerenteDTO.getId());
            contaDTO.setIdCliente(clienteDTO.getId());
            if (clienteDTO.getSalario() >= 2000.0) {
                contaDTO.setLimite(clienteDTO.getSalario() / 2);
            } else {
                contaDTO.setLimite(0.0);
            }
            Response responseConta = (Response) messagingService.sendAndReceiveMessage(QueueConstants.CREATE_CLIENT_ACCOUNT, contaDTO);
            if (!responseConta.getSuccess()) {
                Response responseClienteRemove = (Response) messagingService.sendAndReceiveMessage(QueueConstants.CLIENT_REMOVE, clienteDTO);
                return new ResponseEntity<>(new Response(false, responseConta.getMessage(), null), HttpStatus.valueOf(responseConta.getCode()));
            }else{
                Response responseAddOneToManager = (Response) messagingService.sendAndReceiveMessage(QueueConstants.MANAGER_ADD_ONE, gerenteDTO);
                if (!responseAddOneToManager.getSuccess()) {
                    Response responseClienteRemove = (Response) messagingService.sendAndReceiveMessage(QueueConstants.CLIENT_REMOVE, clienteDTO);
                    return new ResponseEntity<>(new Response(false, responseAddOneToManager.getMessage(), null), HttpStatus.valueOf(responseAddOneToManager.getCode()));
                }
            }
            return new ResponseEntity<>(new Response(true, response.getMessage(), response.getData()), HttpStatus.valueOf(response.getCode()));
        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, "Erro interno ao criar cliente", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("gerente/adicionar")
    @Operation(
            summary = "Endpoint para cadastro de gerente",
            description = "Retorna os dados do gerente adicionado"
    )
    @ApiResponse(responseCode = "403", description = "CPF ou E-mail duplicado", content = @Content(schema = @Schema(implementation = Response.class)))
    public ResponseEntity<Object> inserirCliente(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request ilustrativa") @RequestBody GerenteDTO gerenteDTO) {
        try {
            if (gerenteDTO.getNome() == null || gerenteDTO.getEmail() == null || gerenteDTO.getCpf() == null || gerenteDTO.getTelefone() == null) {
                return new ResponseEntity<>(new Response(false, "Dados do gerente inválidos", null), HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>(new Response(false, "Erro interno ao criar grente", null), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, "Erro interno ao criar grente", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
