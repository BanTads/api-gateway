package com.apigateway.auth.auth.listeners;

import com.apigateway.auth.auth.dto.UserChangeDTO;
import com.apigateway.auth.auth.dto.UserDTO;
import com.apigateway.auth.auth.helper.UserHelper;
import com.apigateway.auth.auth.model.User;
import com.apigateway.auth.auth.repositories.UserRepository;
import com.apigateway.auth.auth.services.MessagingService;
import com.apigateway.auth.auth.utils.Response;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserListener {
    @Autowired
    private MessagingService messagingService;
    @Autowired
    private UserHelper helper;
    @Autowired
    private UserRepository repo;
    private final ObjectMapper objectMapper;
    @Autowired
    public UserListener(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "user.insert")
    public String processUserInsert(String message) {
        try {
            UserDTO userDTO = objectMapper.readValue(message, UserDTO.class);
            System.out.println("Received message: " + userDTO);
            ResponseEntity<Object> responseEntity = helper.saveUser(userDTO);
            System.out.println();
            String responseJson = objectMapper.writeValueAsString(responseEntity.getBody());
            System.out.println("Sending response: " + responseJson);
            return responseJson;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error in processUserInsert", e);
        }
    }

    @RabbitListener(queues = "user.update")
    public String processUserUpdate(String message) {
        try {
            UserChangeDTO userChangeDTO = objectMapper.readValue(message, UserChangeDTO.class);
            System.out.println("Received message: " + userChangeDTO);
            ResponseEntity<Object> responseEntity = helper.updateUser(userChangeDTO);
            System.out.println();
            String responseJson = objectMapper.writeValueAsString(responseEntity.getBody());
            System.out.println("Sending response: " + responseJson);
            return responseJson;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error in processUserInsert", e);
        }
    }
    @RabbitListener(queues = "user.remove")
    public String processUserRemove(String message) {
        try {
            String email = message.trim().replace("\"", "");
            System.out.println("Received message: " + email);
            ResponseEntity<Object> responseEntity = helper.removeUser(email);
            System.out.println();
            String responseJson = objectMapper.writeValueAsString(responseEntity.getBody());
            System.out.println("Sending response: " + responseJson);
            return responseJson;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error in processUserInsert", e);
        }
    }
}
