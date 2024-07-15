package com.apigateway.cliente.cliente.listeners;

import com.apigateway.cliente.cliente.dto.ClienteDTO;
import com.apigateway.cliente.cliente.dto.ClienteInfoRequestDTO;
import com.apigateway.cliente.cliente.model.Cliente;
import com.apigateway.cliente.cliente.repositories.ClienteRepository;
import com.apigateway.cliente.cliente.services.MessagingService;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
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
//    @RabbitListener(queues = "client.get.info")
//    public String handleClienteInfoRequest(ClienteInfoRequestDTO request) {
//        try {
//            System.out.println(request);
//            Cliente cliente = repo.findById(request.getClienteId()).orElse(null);
//            if (cliente == null) {
//                System.out.println("Cliente não encontrado para ID: " + request.getClienteId());
//                return null;
//            }
//            Gson gson = new Gson();
//            String clienteJson = gson.toJson(cliente);
//            System.out.println("Cliente processado: " + clienteJson);
//            return clienteJson;
//        } catch (Exception e) {
//            System.out.println("Erro ao processar informações do cliente: " + e.getMessage());
//            return "error";
//        }
//    }

}
