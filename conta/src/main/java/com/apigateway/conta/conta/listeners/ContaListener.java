package com.apigateway.conta.conta.listeners;

import com.apigateway.conta.conta.constants.QueueConstants;
import com.apigateway.conta.conta.dto.ClienteDTO;
import com.apigateway.conta.conta.dto.ContaDTO;
import com.apigateway.conta.conta.model.Conta;
import com.apigateway.conta.conta.repositories.ContaRepository;
import com.apigateway.conta.conta.services.MessagingService;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ContaListener {
    @Autowired
    private MessagingService messagingService;
    @Autowired
    private ContaRepository repo;
    @Autowired
    private ModelMapper mapper;

    @RabbitListener(queues = QueueConstants.CREATE_CLIENT_ACCOUNT)
    public void criarContaHandler(ClienteDTO clienteDTO) {
        try {
            ContaDTO account = new ContaDTO();
            account.setIdCliente(clienteDTO.getId());
            account.setAprovada(null);
            account.setDataCriacao(new Date());
            if (clienteDTO.getSalario() >= 2000.0) {
                account.setLimite(clienteDTO.getSalario() / 2);
            } else {
                account.setLimite(0.0);
            }

            Conta contaCriada = repo.saveAndFlush(mapper.map(account, Conta.class));
            this.messagingService.sendMessage(QueueConstants.ASSIGN_MANAGER_TO_ACCOUNT, contaCriada.getNumeroConta());
        }catch (Exception e){
            System.out.println(e);
        }
    }
}
