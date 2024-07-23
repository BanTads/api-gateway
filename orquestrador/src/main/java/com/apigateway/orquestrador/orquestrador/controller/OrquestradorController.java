package com.apigateway.orquestrador.orquestrador.controller;

import ch.qos.logback.core.net.server.Client;
import com.apigateway.orquestrador.orquestrador.constants.QueueConstants;
import com.apigateway.orquestrador.orquestrador.dto.*;
import com.apigateway.orquestrador.orquestrador.services.EmailCheckService;
import com.apigateway.orquestrador.orquestrador.services.EmailService;
import com.apigateway.orquestrador.orquestrador.services.MessagingService;
import com.apigateway.orquestrador.orquestrador.utils.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.apache.catalina.User;
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
    @Autowired
    private Gson gson;
    @Autowired
    private EmailCheckService emailCheckService;
    @Autowired
    private EmailService emailService;
    @PostMapping("autocadastro")
    @Operation(
            summary = "Endpoint para autocadastro de cliente",
            description = "Retorna os dados do cliente adicionado"
    )
    @ApiResponse(responseCode = "403", description = "CPF ou E-mail duplicado", content = @Content(schema = @Schema(implementation = Response.class)))
    public ResponseEntity<Object> inserirCliente(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request ilustrativa") @RequestBody ClienteDTO clienteDTO) {
        try {
            GerenteDTO gerenteDTO = new GerenteDTO();
            //gerente com menos contas
            Response responseManager = (Response) messagingService.sendAndReceiveMessage(QueueConstants.MANAGER_MIN_ACCOUNT, clienteDTO);
            //retornando erro caso exista algum para cliente
            if (!responseManager.getSuccess()) {
                System.out.print("Entrou1");
                emailService.sendEmail(clienteDTO.getEmail(), "Erro ao realizar cadastro no BANTADS.", clienteDTO.getNome());
                return new ResponseEntity<>(new Response(false, responseManager.getMessage(), null), HttpStatus.valueOf(responseManager.getCode()));
            }

            boolean isUniqueManager = emailCheckService.isManagerEmailUnique(clienteDTO.getEmail(), null);
            if(!isUniqueManager){
                return new ResponseEntity<>(new Response(false, "Um registro com o mesmo e-mail já existe.", null), HttpStatus.CONFLICT);
            }

            if(responseManager.getData() != null){
                gerenteDTO = objectMapper.convertValue(responseManager.getData(), GerenteDTO.class);
            }

            Response response = (Response) messagingService.sendAndReceiveMessage(QueueConstants.CLIENT_INSERT, clienteDTO);
            //retornando erro caso exista algum para gerente
            if (!response.getSuccess()) {
                System.out.print("Entrou2");
                emailService.sendEmail(clienteDTO.getEmail(), "Erro ao realizar cadastro no BANTADS.", clienteDTO.getNome());
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
                System.out.print("Entrou3");
                emailService.sendEmail(clienteDTO.getEmail(), "Erro ao realizar cadastro no BANTADS.", clienteDTO.getNome());
                Response responseClienteRemove = (Response) messagingService.sendAndReceiveMessage(QueueConstants.CLIENT_REMOVE, clienteDTO);
                return new ResponseEntity<>(new Response(false, responseConta.getMessage(), null), HttpStatus.valueOf(responseConta.getCode()));
            }else{
                Response responseAddOneToManager = (Response) messagingService.sendAndReceiveMessage(QueueConstants.MANAGER_ADD_ONE, gerenteDTO);
                if (!responseAddOneToManager.getSuccess()) {
                    System.out.print("Entrou4");
                    emailService.sendEmail(clienteDTO.getEmail(), "Erro ao realizar cadastro no BANTADS.", clienteDTO.getNome());
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

    @PutMapping("/cliente/atualizar/{id}")
    @ApiResponse(responseCode = "403", description = "CPF ou E-mail duplicado", content = @Content(schema = @Schema(implementation = Response.class)))
    @Operation(summary = "Atualiza um cliente existente pelo ID")
    public ResponseEntity<Object> atualizarCliente(@PathVariable Long id, @RequestBody ClienteDTO clienteDTO) {
        try {
            clienteDTO.setId(id);
            ContaDTO contaDTO = new ContaDTO();

            UserChangeDTO userChangeDTO = new UserChangeDTO();
            ClienteDTO oldClientDTO = new ClienteDTO();

            String jsonOldCliente = (String) messagingService.sendAndReceiveMessageSimple("client.get.info", id);
            oldClientDTO = gson.fromJson(jsonOldCliente, ClienteDTO.class);

            boolean isUniqueClient = emailCheckService.isClientEmailUnique(clienteDTO.getEmail(), id);
            if(!isUniqueClient){
                return new ResponseEntity<>(new Response(false, "Um registro com o mesmo e-mail já existe.", null), HttpStatus.CONFLICT);
            }

            //atualizando o e-mail do usuário
            userChangeDTO.setOldEmail(oldClientDTO.getEmail());
            userChangeDTO.setNewEmail(clienteDTO.getEmail());
            if(!oldClientDTO.getEmail().equals(clienteDTO.getEmail())){
                Response responseUser = (Response) messagingService.sendAndReceiveMessage(QueueConstants.USER_UPDATE, userChangeDTO);
                if (!responseUser.getSuccess()) {
                    return new ResponseEntity<>(new Response(false, responseUser.getMessage(), null), HttpStatus.valueOf(responseUser.getCode()));
                }
            }

            //calculando novo saldo da conta
            if (Float.compare(clienteDTO.getSalario(), 0) != 0) {
                Double novoLimite = clienteDTO.getSalario() >= 2000.0 ? clienteDTO.getSalario() / 2 : 0.0;
                String jsonConta = (String) messagingService.sendAndReceiveMessageSimple("conta.get.info", clienteDTO.getId());
                contaDTO = gson.fromJson(jsonConta, ContaDTO.class);

                //Consultando saldo da conta
                String jsonSaldo = (String) messagingService.sendAndReceiveMessageSimple("conta.get.saldo", contaDTO.getNumeroConta());
                SaldoLimiteDTO saldoLimiteDTO = gson.fromJson(jsonSaldo, SaldoLimiteDTO.class);

                Double saldoAtual = saldoLimiteDTO.getSaldo();
                saldoAtual = (saldoAtual != null) ? saldoAtual : 0.00;

                // Ajusta o limite baseado no saldo negativo
                if (saldoAtual < 0 && novoLimite < Math.abs(saldoAtual)) {
                    contaDTO.setLimite(Math.abs(saldoAtual));
                } else {
                    contaDTO.setLimite(novoLimite);
                }
            }

            Response responseClient = (Response) messagingService.sendAndReceiveMessage(QueueConstants.CLIENT_UPDATE, clienteDTO);
            ClienteDTO oldClienteDTO = new ClienteDTO();
            //retornando erro caso exista algum para gerente
            if (!responseClient.getSuccess()) {
                return new ResponseEntity<>(new Response(false, responseClient.getMessage(), null), HttpStatus.valueOf(responseClient.getCode()));
            }

            if (Float.compare(clienteDTO.getSalario(), 0) != 0) {
                Response responseConta = (Response) messagingService.sendAndReceiveMessage(QueueConstants.CONTA_UPDATE_LIMIT, contaDTO);
                if (!responseConta.getSuccess()) {
                    return new ResponseEntity<>(new Response(false, responseConta.getMessage(), null), HttpStatus.valueOf(responseConta.getCode()));
                }
            }

            return new ResponseEntity<>(new Response(true, responseClient.getMessage(), responseClient.getData()), HttpStatus.valueOf(responseClient.getCode()));
        } catch (DataIntegrityViolationException e) {
            String mensagemErro = "Um registro com o mesmo CPF ou e-mail já existe.";
            return new ResponseEntity<>(new Response(false, mensagemErro, null), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(new Response(false, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("gerente/adicionar")
    @Operation(
            summary = "Endpoint para cadastro de gerente",
            description = "Retorna os dados do gerente adicionado"
    )
    @ApiResponse(responseCode = "403", description = "CPF ou E-mail duplicado", content = @Content(schema = @Schema(implementation = Response.class)))
    public ResponseEntity<Object> inserirGerente(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request ilustrativa") @RequestBody GerenteDTO gerenteDTO) {
        try {
            if (gerenteDTO.getNome() == null || gerenteDTO.getEmail() == null || gerenteDTO.getCpf() == null || gerenteDTO.getTelefone() == null) {
                return new ResponseEntity<>(new Response(false, "Dados do gerente inválidos", null), HttpStatus.BAD_REQUEST);
            }

            boolean isUniqueClient = emailCheckService.isClientEmailUnique(gerenteDTO.getEmail(), null);
            if(!isUniqueClient){
                return new ResponseEntity<>(new Response(false, "Um registro com o mesmo e-mail já existe.", null), HttpStatus.CONFLICT);
            }

            GerenteDTO managerMaxAccountDTO = new GerenteDTO();
            Response managerMaxAccount = (Response) messagingService.sendAndReceiveMessage(QueueConstants.MANAGER_MAX_ACCOUNT, gerenteDTO);

            //se der algum erro ao buscar o gerente com maximo de contas e se for diferente de 404, se retornar 404 significa que é o primeiro gerente
            //ou se existir um gerente, ele só tem uma conta
            if (!managerMaxAccount.getSuccess() && managerMaxAccount.getCode() != 404) {
                return new ResponseEntity<>(new Response(false, managerMaxAccount.getMessage(), null), HttpStatus.valueOf(managerMaxAccount.getCode()));
            }else if(managerMaxAccount.getSuccess() && managerMaxAccount.getData() != null){
                //se entrar o else, significa que encontrou um gerente com "mais contas" e adiciona pra esse novo gerente, "uma quantidade de conta"
                managerMaxAccountDTO = objectMapper.convertValue(managerMaxAccount.getData(), GerenteDTO.class);
                gerenteDTO.setQuantidadeContas(1);
            }

            Response responseManager = (Response) messagingService.sendAndReceiveMessage(QueueConstants.MANAGER_INSERT, gerenteDTO);
            //retornando erro caso exista algum para gerente
            if (!responseManager.getSuccess()) {
                return new ResponseEntity<>(new Response(false, responseManager.getMessage(), null), HttpStatus.valueOf(responseManager.getCode()));
            }

            if(responseManager.getSuccess() && responseManager.getData() != null){
                gerenteDTO = objectMapper.convertValue(responseManager.getData(), GerenteDTO.class);
            }

            UserDTO userDTO = new UserDTO();
            userDTO.setEmail(gerenteDTO.getEmail());
            userDTO.setNome(gerenteDTO.getNome());
            userDTO.setCargo("GERENTE");
            Response responseUsuarioCriado = (Response) messagingService.sendAndReceiveMessage("user.insert",  userDTO);
            if (!responseUsuarioCriado.getSuccess()) {
                Response responseManagerRemove = (Response) messagingService.sendAndReceiveMessage(QueueConstants.REMOVE_MANAGER, gerenteDTO);
                return new ResponseEntity<>(new Response(false, "Erro ao cadastrar usuário: " + responseUsuarioCriado.getMessage(), null), HttpStatus.valueOf(responseUsuarioCriado.getCode()));
            }

            if(managerMaxAccountDTO.getId() != null) { //significa que temos pegar uma conta e atribuir do gerente antigo para o novo
                //primeiro, removemos uma quantidade do cliente com mais contas
                Response managerLessOne = (Response) messagingService.sendAndReceiveMessage(QueueConstants.MANAGER_REMOVE_ONE, managerMaxAccountDTO);

                if (!managerLessOne.getSuccess()) {
                    return new ResponseEntity<>(new Response(false, managerLessOne.getMessage(), null), HttpStatus.valueOf(managerLessOne.getCode()));
                }else{ //se deu tudo certo, atribuimos à conta o novo id gerente
                    GerenteReassignmentDTO gerenteReassignmentDTO = new GerenteReassignmentDTO(managerMaxAccountDTO.getId(), gerenteDTO.getId(), true);
                    Response accountReassigned = (Response) messagingService.sendAndReceiveMessage(QueueConstants.REASSIGN_MANAGER_TO_ACCOUNT, gerenteReassignmentDTO);

                    if (!accountReassigned.getSuccess()) { //se caso falhou a reatribuição de conta, voltamos a quantidade de cliente, e o gerente que foi criado é removido
                        Response responseAddOneToManager = (Response) messagingService.sendAndReceiveMessage(QueueConstants.MANAGER_ADD_ONE, managerMaxAccountDTO);
                        Response responseManagerRemove = (Response) messagingService.sendAndReceiveMessage(QueueConstants.REMOVE_MANAGER, gerenteDTO);
                        Response responseUserRemove = (Response) messagingService.sendAndReceiveMessage(QueueConstants.REMOVE_USER, gerenteDTO.getEmail());
                        return new ResponseEntity<>(new Response(false, responseManager.getMessage(), null), HttpStatus.valueOf(responseManager.getCode()));
                    }
                }
            }

            return new ResponseEntity<>(new Response(true, responseManager.getMessage(), responseManager.getData()), HttpStatus.valueOf(responseManager.getCode()));
        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, "Erro interno ao criar gerente", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/gerente/atualizar/{id}")
    @ApiResponse(responseCode = "403", description = "CPF ou E-mail duplicado", content = @Content(schema = @Schema(implementation = Response.class)))
    @Operation(summary = "Atualiza um gerente existente pelo ID")
    public ResponseEntity<Object> atualizarGerente(@PathVariable Long id, @RequestBody GerenteDTO gerenteDTO) {
        try {
            boolean isUniqueManager = emailCheckService.isManagerEmailUnique(gerenteDTO.getEmail(), id);
            if(!isUniqueManager){
                return new ResponseEntity<>(new Response(false, "Um registro com o mesmo e-mail já existe.", null), HttpStatus.CONFLICT);
            }

            //atualização de usuário
            UserChangeDTO userChangeDTO = new UserChangeDTO();
            GerenteDTO oldManagerDTO = new GerenteDTO();
            String jsonOldManager = (String) messagingService.sendAndReceiveMessageSimple("gerente.get.info", id);
            System.out.print(jsonOldManager);
            oldManagerDTO = gson.fromJson(jsonOldManager, GerenteDTO.class);

            userChangeDTO.setOldEmail(oldManagerDTO.getEmail());
            userChangeDTO.setNewEmail(gerenteDTO.getEmail());
            if(!oldManagerDTO.getEmail().equals(gerenteDTO.getEmail())){
                Response responseUser = (Response) messagingService.sendAndReceiveMessage(QueueConstants.USER_UPDATE, userChangeDTO);
                if (!responseUser.getSuccess()) {
                    return new ResponseEntity<>(new Response(false, responseUser.getMessage(), null), HttpStatus.valueOf(responseUser.getCode()));
                }
            }

            GerenteDTO newGerenteDTO = new GerenteDTO();
            newGerenteDTO.setId(id);
            newGerenteDTO.setEmail(gerenteDTO.getEmail());
            newGerenteDTO.setTelefone(gerenteDTO.getTelefone());
            newGerenteDTO.setNome(gerenteDTO.getNome());
            Response responseManager = (Response) messagingService.sendAndReceiveMessage(QueueConstants.MANAGER_UPDATE, newGerenteDTO);
            //retornando erro caso exista algum para gerente
            if (!responseManager.getSuccess()) {
                return new ResponseEntity<>(new Response(false, responseManager.getMessage(), null), HttpStatus.valueOf(responseManager.getCode()));
            }
            //todo atualizar usuário do gerente
            return new ResponseEntity<>(new Response(true, responseManager.getMessage(), responseManager.getData()), HttpStatus.valueOf(responseManager.getCode()));
        } catch (DataIntegrityViolationException e) {
            String mensagemErro = "Um registro com o mesmo CPF ou e-mail já existe.";
            return new ResponseEntity<>(new Response(false, mensagemErro, null), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(new Response(false, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/gerente/remover/{id}")
    @Operation(summary = "Remove um gerente pelo ID")
    public ResponseEntity<Object> removerGerente(@PathVariable Long id, @RequestBody GerenteDTO gerenteDTO) {
        try {
            GerenteDTO newManagerDTO = new GerenteDTO();
            gerenteDTO.setId(id);
            Response responseNewManager = (Response) messagingService.sendAndReceiveMessage(QueueConstants.VERIFY_AND_FIND_NEW_MANAGER, gerenteDTO);
            //retornando erro caso exista algum para gerente
            if (!responseNewManager.getSuccess()) {
                return new ResponseEntity<>(new Response(false, responseNewManager.getMessage(), null), HttpStatus.valueOf(responseNewManager.getCode()));
            }else{
                newManagerDTO = objectMapper.convertValue(responseNewManager.getData(), GerenteDTO.class);
                GerenteReassignmentDTO gerenteReassignmentDTO = new GerenteReassignmentDTO(gerenteDTO.getId(), newManagerDTO.getId(), false);
                Response accountsReassigned = (Response) messagingService.sendAndReceiveMessage(QueueConstants.REASSIGN_MANAGER_TO_ACCOUNT, gerenteReassignmentDTO);
                if (!accountsReassigned.getSuccess()) {
                    return new ResponseEntity<>(new Response(false, responseNewManager.getMessage(), null), HttpStatus.valueOf(responseNewManager.getCode()));
                }else{
                    //se as contas foram reatribuidas com sucesso, então, remove o gerente antigo e faz um update no novo setando a nova quantidade de contas
                    Response responseManagerRemove = (Response) messagingService.sendAndReceiveMessage(QueueConstants.REMOVE_MANAGER, gerenteDTO);
                    Response responseManager = (Response) messagingService.sendAndReceiveMessage(QueueConstants.MANAGER_UPDATE, newManagerDTO);
                    if(responseManagerRemove.getData() != null){
                        GerenteDTO gerenteRemovedDTO = new GerenteDTO();
                        gerenteRemovedDTO = objectMapper.convertValue(responseManagerRemove.getData(), GerenteDTO.class);

                        Response responseManagerRemoveUser = (Response) messagingService.sendAndReceiveMessage(QueueConstants.REMOVE_USER, gerenteRemovedDTO.getEmail());
                        if (!responseManagerRemoveUser.getSuccess()) {
                            return new ResponseEntity<>(new Response(false, "Houve um erro ao remover o usuário do gerente. As demais alterações foram realizadas.", null), HttpStatus.valueOf(responseManagerRemoveUser.getCode()));
                        }
                    }
                }
            }
            return new ResponseEntity<>(new Response(true, "Usuário e gerente removido com sucesso", null), HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            String mensagemErro = "Um registro com o mesmo CPF ou e-mail já existe.";
            return new ResponseEntity<>(new Response(false, mensagemErro, null), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(new Response(false, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
