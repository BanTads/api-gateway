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
import java.util.Optional;
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

    @RabbitListener(queues = "client.get.info")
    public String getClientInfo(Long idCliente) {
        try {
            Cliente cliente = repo.findById(idCliente).orElse(null);
            if (cliente == null) {
                System.out.println("Cliente não encontrado para ID: " + idCliente);
                return null;
            }
            Gson gson = new Gson();
            String clienteJson = gson.toJson(cliente);
            System.out.println("Cliente processado: " + clienteJson);
            return clienteJson;
        } catch (Exception e) {
            System.out.println("Erro ao processar informações da conta: " + e.getMessage());
            return "error";
        }
    }

    @RabbitListener(queues = "client.check.email")
    public String checkEmail(String email) {
        try {
            Cliente cliente = repo.findByEmail(email);
            if (cliente == null) {
                return null;
            }
            Gson gson = new Gson();
            String userJson = gson.toJson(cliente);
            return userJson;
        } catch (Exception e) {
            System.out.println("Erro ao processar informações do gerente: " + e.getMessage());
            return "error";
        }
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

    @RabbitListener(queues = "client.update")
    public String processClientUpdate(String message) {
        try {
            ClienteDTO clienteDTO = objectMapper.readValue(message, ClienteDTO.class);
            System.out.println("Received message: " + clienteDTO);
            ResponseEntity<Object> responseEntity = helper.updateClient(clienteDTO);
            System.out.println();
            String responseJson = objectMapper.writeValueAsString(responseEntity.getBody());
            System.out.println("Sending response: " + responseJson);
            return responseJson;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error in processClientUpdate", e);
        }
    }

    @RabbitListener(queues = "client.remove")
    public String processClientRemove(String message) {
        try {
            ClienteDTO clienteDTO = objectMapper.readValue(message, ClienteDTO.class);
            System.out.println("Received message: " + clienteDTO);
            ResponseEntity<Object> responseEntity = helper.deleteCliente(clienteDTO);
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
