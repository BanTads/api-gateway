package com.apigateway.orquestrador.orquestrador.listeners;

import com.apigateway.orquestrador.orquestrador.constants.QueueConstants;
import com.apigateway.orquestrador.orquestrador.dto.ClienteDTO;
import com.apigateway.orquestrador.orquestrador.services.MessagingService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class ClienteListener {

    @Autowired
    private MessagingService messagingService;

//    @RabbitListener(queues = QueueConstants.CLIENTE_CREATED)
//    public void receiveMessage(ClienteDTO clienteDTO) {
//        this.messagingService.sendMessage(QueueConstants.CREATE_CLIENT_ACCOUNT, clienteDTO);
//    }
}
