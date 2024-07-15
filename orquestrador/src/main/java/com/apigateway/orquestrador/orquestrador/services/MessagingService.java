package com.apigateway.orquestrador.orquestrador.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessagingService {
    @Autowired
    private RabbitTemplate template;

    public void sendMessage(String queueName, Object message){
//        this.template.convertSendAndReceive(queueName, message);
        this.template.convertAndSend(queueName, message);
    }
}
