package com.apigateway.orquestrador.orquestrador.services;

import com.apigateway.orquestrador.orquestrador.utils.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessagingService {
    @Autowired
    private RabbitTemplate template;
    private final ObjectMapper objectMapper;

    public void sendMessage(String queueName, Object message){
//        this.template.convertSendAndReceive(queueName, message);
        this.template.convertAndSend(queueName, message);
    }

    public MessagingService(RabbitTemplate rabbitTemplate) {
        this.template = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public String convertToJson(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }

    public <T> T convertFromJson(String json, Class<T> clazz) throws Exception {
        return objectMapper.readValue(json, clazz);
    }

    public Object sendAndReceiveMessage(String routingKey, Object message) {
        try {
            String jsonMessage = convertToJson(message);
            System.out.println("Sending message: " + jsonMessage);
            String jsonResponse = (String) template.convertSendAndReceive(routingKey, jsonMessage);
            System.out.println("Received response: " + jsonResponse);
            return convertFromJson(jsonResponse, Response.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error in sendAndReceiveMessage", e);
        }
    }
}
