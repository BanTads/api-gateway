package com.apigateway.cliente.cliente.listeners;

import com.apigateway.cliente.cliente.dto.ClienteDTO;
import com.apigateway.cliente.cliente.dto.ClienteInfoRequestDTO;
import com.apigateway.cliente.cliente.dto.EnderecoDTO;
import com.apigateway.cliente.cliente.helpers.ClienteHelper;
import com.apigateway.cliente.cliente.model.Cliente;
import com.apigateway.cliente.cliente.repositories.ClienteRepository;
import com.apigateway.cliente.cliente.services.MessagingService;
import com.apigateway.cliente.cliente.utils.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import com.google.gson.Gson;

import java.util.List;
import java.util.Random;

@Component
public class ClienteListener {
    @Autowired
    private MessagingService messagingService;
    @Autowired
    private ClienteRepository repo;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private ClienteHelper helper;
    private final ObjectMapper objectMapper;
    @Autowired
    public ClienteListener(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "client.insert")
    public String processClientInsert(String message) {
        try {
            ClienteDTO clienteDTO = objectMapper.readValue(message, ClienteDTO.class);
            System.out.println("Received message: " + clienteDTO);
            ResponseEntity<Object> responseEntity = helper.saveClient(clienteDTO);
            System.out.println();
            String responseJson = objectMapper.writeValueAsString(responseEntity.getBody());
            System.out.println("Sending response: " + responseJson);
            return responseJson;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error in processClientInsert", e);
        }
    }


}
