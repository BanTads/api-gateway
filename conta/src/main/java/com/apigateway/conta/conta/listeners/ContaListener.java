package com.apigateway.conta.conta.listeners;

import com.apigateway.conta.conta.constants.QueueConstants;
import com.apigateway.conta.conta.dto.ClienteDTO;
import com.apigateway.conta.conta.dto.ContaDTO;
import com.apigateway.conta.conta.dto.GerenteAssignmentDTO;
import com.apigateway.conta.conta.dto.GerenteReassignmentDTO;
import com.apigateway.conta.conta.helpers.ContaHelper;
import com.apigateway.conta.conta.model.Conta;
import com.apigateway.conta.conta.repositories.ContaRepository;
import com.apigateway.conta.conta.services.MessagingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Component
public class ContaListener {
    @Autowired
    private MessagingService messagingService;
    @Autowired
    private ContaRepository repo;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private ContaHelper helper;
    private final ObjectMapper objectMapper;
    @Autowired
    public ContaListener(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = QueueConstants.CREATE_CLIENT_ACCOUNT)
    public String criarContaHandler(String message) {
        try {
            ContaDTO contaDTO = objectMapper.readValue(message, ContaDTO.class);
            System.out.println("Received message: " + contaDTO);
            ResponseEntity<Object> responseEntity = helper.saveAccount(contaDTO);
            System.out.println();
            String responseJson = objectMapper.writeValueAsString(responseEntity.getBody());
            System.out.println("Sending response: " + responseJson);
            return responseJson;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error in createClienteAccount", e);
        }
    }

    @RabbitListener(queues = QueueConstants.REASSIGN_MANAGER_TO_ACCOUNT)
    public void reassignGerente(GerenteReassignmentDTO message) {
        List<Conta> contas = repo.findByIdGerente(message.getOldGerenteId());
        if (message.getGerenteCriado()) {
            // Atribuir apenas uma conta aleatória ao novo gerente
            if (!contas.isEmpty()) {
                Random random = new Random();
                Conta contaSelecionada = contas.get(random.nextInt(contas.size()));
                contaSelecionada.setIdGerente(message.getNewGerenteId());
                repo.save(contaSelecionada);
            }
        } else {
            // Atribuir todas as contas ao novo gerente
            for (Conta conta : contas) {
                conta.setIdGerente(message.getNewGerenteId());
                repo.save(conta);
            }
            this.messagingService.sendMessage(QueueConstants.REASSIGN_MANAGER_ACCOUNT_COMPLETED, message);
        }
    }

    @RabbitListener(queues = QueueConstants.ASSIGN_MANAGER_ACCOUNT_COMPLETED)
    public void assignGerente(GerenteAssignmentDTO message) {
        Optional<Conta> contaOpt = repo.findById(message.getNumeroConta());
        if (contaOpt.isPresent()) {
            Conta conta = contaOpt.get();
            conta.setIdGerente(message.getGerenteId());
            repo.save(conta);
        } else {
            System.err.println("Conta com ID " + message.getNumeroConta() + " não encontrada.");
        }
    }

    @RabbitListener(queues = "conta.get.info")
    public String contaInfo(Long idCliente) {
        try {
            Conta conta = repo.findByIdCliente(idCliente);
            if (conta == null) {
                System.out.println("Conta não encontrada para ID: " + idCliente);
                return null;
            }
            Gson gson = new Gson();
            String contaJson = gson.toJson(conta);
            System.out.println("Conta processada: " + contaJson);
            return contaJson;
        } catch (Exception e) {
            System.out.println("Erro ao processar informações da conta: " + e.getMessage());
            return "error";
        }
    }

    @RabbitListener(queues = "conta.get.info.gerente")
    public String contasInfo(Long idGerente) {
        try {
            List<Conta> contas = repo.findByIdGerente(idGerente);
            if (contas == null || contas.isEmpty()) {
                System.out.println("Conta não encontrada para ID: " + idGerente);
                return null;
            }

            String contasJson = objectMapper.writeValueAsString(contas);
            System.out.println("Contas processadas: " + contasJson);
            return contasJson;
        } catch (Exception e) {
            System.out.println("Erro ao processar informações das contas: " + e.getMessage());
            return "error";
        }
    }
}
