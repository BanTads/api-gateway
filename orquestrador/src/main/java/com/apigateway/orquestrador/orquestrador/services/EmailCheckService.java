package com.apigateway.orquestrador.orquestrador.services;

import com.apigateway.orquestrador.orquestrador.dto.ClienteDTO;
import com.apigateway.orquestrador.orquestrador.dto.GerenteDTO;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;

@Service
public class EmailCheckService {
    private final MessagingService messagingService;
    private final Gson gson;

    public EmailCheckService(MessagingService messagingService, Gson gson) {
        this.messagingService = messagingService;
        this.gson = gson;
    }

    public boolean isClientEmailUnique(String email, Long id) {
        // Verificar se o email já existe na tabela de usuários
        String jsonManager = (String) messagingService.sendAndReceiveMessageSimple("client.check.email", email);
        ClienteDTO existingClient = gson.fromJson(jsonManager, ClienteDTO.class);
        if (existingClient != null && (id == null || id == 0 || !existingClient.getId().equals(id))) {
            return false;
        }
        return true;
    }

    public boolean isManagerEmailUnique(String email, Long id) {
        // Verificar se o email já existe na tabela de usuários
        String jsonManager = (String) messagingService.sendAndReceiveMessageSimple("manager.check.email", email);
        GerenteDTO existingManager = gson.fromJson(jsonManager, GerenteDTO.class);
        if (existingManager != null && (id == null || id == 0 || !existingManager.getId().equals(id))) {
            return false;
        }
        return true;
    }
}