package com.apigateway.cliente.cliente.service;

import com.apigateway.cliente.cliente.dto.ClienteDTO;
import com.apigateway.cliente.cliente.dto.EnderecoDTO;
import com.apigateway.cliente.cliente.repositories.ClienteRepository;
import com.apigateway.cliente.cliente.repositories.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnderecoService {
    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    public EnderecoService(EnderecoRepository enderecoRepository) {
        this.enderecoRepository = enderecoRepository;
    }

    public Long adicionarEndereco(EnderecoDTO enderecoDTO) {
        return enderecoRepository.adicionarEndereco(enderecoDTO);
    }
}
