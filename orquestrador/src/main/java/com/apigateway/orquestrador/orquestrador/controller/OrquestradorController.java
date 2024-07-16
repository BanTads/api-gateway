package com.apigateway.orquestrador.orquestrador.controller;

import com.apigateway.orquestrador.orquestrador.constants.QueueConstants;
import com.apigateway.orquestrador.orquestrador.dto.ClienteDTO;
import com.apigateway.orquestrador.orquestrador.services.MessagingService;
import com.apigateway.orquestrador.orquestrador.utils.Response;
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

    @PostMapping("autocadastro")
    @Operation(
            summary = "Endpoint para autocadastro de cliente",
            description = "Retorna os dados do cliente adicionado"
    )
    @ApiResponse(responseCode = "403", description = "CPF ou E-mail duplicado", content = @Content(schema = @Schema(implementation = Response.class)))
    public ResponseEntity<Object> inserir(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request ilustrativa") @RequestBody ClienteDTO clienteDTO) {
        try {
            Response response = (Response) messagingService.sendAndReceiveMessage(QueueConstants.CLIENT_INSERT, clienteDTO);
            System.out.println(response.getSuccess());
            //adicionando cliente
            if (!response.getSuccess()) {
                return new ResponseEntity<>(new Response(false, response.getMessage(), null), HttpStatus.valueOf(response.getCode()));
            }

            Response responseConta = (Response) messagingService.sendAndReceiveMessage(QueueConstants.CREATE_CLIENT_ACCOUNT, clienteDTO);
            System.out.println(responseConta);

            return new ResponseEntity<>(new Response(true, response.getMessage(), response.getData()), HttpStatus.valueOf(response.getCode()));
        } catch (DataIntegrityViolationException e) {
            String mensagemErro = "Um registro com o mesmo CPF ou e-mail já existe.";
            return new ResponseEntity<>(new Response(false, mensagemErro, null), HttpStatus.CONFLICT);
        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, mensagemErro, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
