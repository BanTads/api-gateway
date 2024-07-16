package com.apigateway.gerente.gerente.listeners;

import com.apigateway.gerente.gerente.constants.QueueConstants;
import com.apigateway.gerente.gerente.dto.GerenteAssignmentDTO;
import com.apigateway.gerente.gerente.dto.GerenteReassignmentDTO;
import com.apigateway.gerente.gerente.model.Gerente;
import com.apigateway.gerente.gerente.repositories.GerenteRepository;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
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

    @RabbitListener(queues = QueueConstants.ASSIGN_MANAGER_TO_ACCOUNT)
    @Transactional(rollbackFor = Exception.class)
    public void assignManagerToAccount(Long numeroConta) {
        List<Gerente> gerentes = repo.findAll();
        if (gerentes.isEmpty()) {
            return;
        }

        Gerente gerenteComMenosContas = gerentes.stream()
                .min(Comparator.comparingInt(Gerente::getQuantidadeContas))
                .orElse(null);

        if (gerenteComMenosContas == null) {
            return;
        }

//        gerenteComMenosContas.setQuantidadeContas(gerenteComMenosContas.getQuantidadeContas() + 1);
//        repo.saveAndFlush(gerenteComMenosContas);

        GerenteAssignmentDTO assignmentDTO = new GerenteAssignmentDTO(gerenteComMenosContas.getId(), numeroConta);
//        messagingService.sendMessage(QueueConstants.ASSIGN_MANAGER_ACCOUNT_COMPLETED, assignmentDTO);

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
