package com.apigateway.orquestrador.orquestrador.listeners;

import com.apigateway.orquestrador.orquestrador.services.MessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContaListener {
    @Autowired
    private MessagingService messagingService;
}
