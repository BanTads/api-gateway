package com.apigateway.cliente.cliente.service;

import com.apigateway.cliente.cliente.dto.ClienteDTO;
import com.apigateway.cliente.cliente.dto.EnderecoDTO;
import com.apigateway.cliente.cliente.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;
    private EnderecoService enderecoService;

//    @Autowired
//    private ModelMapper modelMapperCliente;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository, EnderecoService enderecoService) {
        this.clienteRepository = clienteRepository;
        this.enderecoService = enderecoService;
    }

    public List<ClienteDTO> getAllClients() {
        return clienteRepository.getAllClients();
    }

    @Transactional
    public ClienteDTO adicionarCliente(ClienteDTO clienteDTO) {
        try {
            EnderecoDTO enderecoDTO = clienteDTO.getEndereco();
            Long idEndereco = enderecoService.adicionarEndereco(enderecoDTO);
            enderecoDTO.setId(idEndereco);
            Long idCliente = clienteRepository.adicionarCliente(clienteDTO);
            clienteDTO.setId(idCliente);
            return clienteDTO;
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao adicionar cliente");
        }
    }

    public boolean existeClienteComCpf(String cpf, String email) {
        return clienteRepository.existeClienteComCpf(cpf, email);
    }
}