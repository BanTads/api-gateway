package com.apigateway.orquestrador.orquestrador.listeners;

import com.apigateway.orquestrador.orquestrador.constants.QueueConstants;
import com.apigateway.orquestrador.orquestrador.dto.GerenteDTO;
import com.apigateway.orquestrador.orquestrador.dto.GerenteReassignmentDTO;
import com.apigateway.orquestrador.orquestrador.services.MessagingService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GerenteListener {
    @Autowired
    private MessagingService messagingService;
    @RabbitListener(queues = QueueConstants.MANAGER_CREATED)
    public void managerCreated(GerenteDTO gerenteDTO) {
        this.messagingService.sendMessage(QueueConstants.CREATE_MANAGER_USER, gerenteDTO);
    }

    @RabbitListener(queues = QueueConstants.MANAGER_EDITED)
    public void managerEdited(GerenteDTO gerenteDTO) {
        this.messagingService.sendMessage(QueueConstants.UPDATE_MANAGER_USER, gerenteDTO);
    }

    @RabbitListener(queues = QueueConstants.REASSIGN_MANAGER)
    public void managerReassignment(GerenteReassignmentDTO message) {
        this.messagingService.sendMessage(QueueConstants.REASSIGN_MANAGER_TO_ACCOUNT, message);
    }

    @RabbitListener(queues = QueueConstants.REASSIGN_MANAGER_ACCOUNT_COMPLETED)
    public void managerRemoved(GerenteReassignmentDTO message) {
        this.messagingService.sendMessage(QueueConstants.REMOVE_MANAGER, message);
    }

    @RabbitListener(queues = QueueConstants.MANAGER_REMOVED)
    public void managerRemoved(String email) {
        this.messagingService.sendMessage(QueueConstants.REMOVE_MANAGER_USER, email);
    }
}
