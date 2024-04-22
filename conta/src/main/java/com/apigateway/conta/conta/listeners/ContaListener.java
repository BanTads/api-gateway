package com.apigateway.conta.conta.listeners;

import com.apigateway.conta.conta.constants.QueueConstants;
import com.apigateway.conta.conta.dto.ClienteDTO;
import com.apigateway.conta.conta.services.MessagingService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContaListener {
    @Autowired
    private MessagingService messagingService;

    @RabbitListener(queues = QueueConstants.CREATE_CLIENT_ACCOUNT)
    public void receiveMessage(ClienteDTO clienteDTO) {
        System.out.println(clienteDTO);
//        this.messagingService.sendMessage(QueueConstants.CREATE_CLIENT_ACCOUNT, clienteDTO);
    }
}
