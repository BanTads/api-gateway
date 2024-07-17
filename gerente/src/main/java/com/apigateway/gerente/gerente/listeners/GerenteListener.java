package com.apigateway.gerente.gerente.listeners;

import com.apigateway.gerente.gerente.constants.QueueConstants;
import com.apigateway.gerente.gerente.dto.GerenteAssignmentDTO;
import com.apigateway.gerente.gerente.dto.GerenteDTO;
import com.apigateway.gerente.gerente.dto.GerenteReassignmentDTO;
import com.apigateway.gerente.gerente.helpers.GerenteHelper;
import com.apigateway.gerente.gerente.model.Gerente;
import com.apigateway.gerente.gerente.repositories.GerenteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import com.apigateway.gerente.gerente.services.MessagingService;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class GerenteListener {
    @Autowired
    private MessagingService messagingService;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private GerenteRepository repo;
    @Autowired
    private GerenteHelper helper;
    private final ObjectMapper objectMapper;
    @Autowired
    public GerenteListener(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = QueueConstants.MANAGER_MIN_ACCOUNT)
    @Transactional(rollbackFor = Exception.class)
    public String getGerenteMinAccount() {
        try {
            ResponseEntity<Object> responseEntity = helper.getManagerMinAccount();
            System.out.println();
            String responseJson = objectMapper.writeValueAsString(responseEntity.getBody());
            System.out.println("Sending response: " + responseJson);
            return responseJson;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error in getGerenteInsert", e);
        }
    }

    @RabbitListener(queues = QueueConstants.REMOVE_MANAGER)
    @Transactional(rollbackFor = Exception.class)
    public void removeManager(GerenteReassignmentDTO message) {
        Long gerenteId = message.getOldGerenteId();
        Optional<Gerente> gerenteOpt = repo.findById(gerenteId);
        if (gerenteOpt.isPresent()) {
            Gerente gerenteToRemove = gerenteOpt.get();

            repo.deleteById(gerenteId);
            messagingService.sendMessage(QueueConstants.MANAGER_REMOVED, gerenteToRemove.getEmail());
        } else {
            System.err.println("Gerente com ID " + gerenteId + " não encontrado.");
        }
    }

    @RabbitListener(queues = "manager.add.one")
    @Transactional(rollbackFor = Exception.class)
    public String managerAddOne(String message) {
        try {
            GerenteDTO gerenteDTO = objectMapper.readValue(message, GerenteDTO.class);
            ResponseEntity<Object> responseEntity = helper.addOneClienteToGerente(gerenteDTO);
            System.out.println();
            String responseJson = objectMapper.writeValueAsString(responseEntity.getBody());
            System.out.println("Sending response: " + responseJson);
            return responseJson;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error in getGerenteInsert", e);
        }
    }

    @RabbitListener(queues = "gerente.get.info")
    public String gerenteInfo(Long idGerente) {
        try {
            Gerente gerente = repo.findById(idGerente).orElse(null);
            if (gerente == null) {
                System.out.println("Conta não encontrada para ID: " + idGerente);
                return null;
            }
            Gson gson = new Gson();
            String gerenteJson = gson.toJson(gerente);
            System.out.println("Gerente processado: " + gerenteJson);
            return gerenteJson;
        } catch (Exception e) {
            System.out.println("Erro ao processar informações do gerente: " + e.getMessage());
            return "error";
        }
    }
}
