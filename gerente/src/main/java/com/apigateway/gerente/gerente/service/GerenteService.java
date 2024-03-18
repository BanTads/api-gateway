package com.apigateway.gerente.gerente.service;

import com.apigateway.cliente.cliente.dto.ClienteDTO;
import com.apigateway.gerente.gerente.dto.GerenteDTO;
import com.apigateway.gerente.gerente.repositories.GerenteRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GerenteService {
    @Autowired
    private GerenteRepository gerenteRepository;

    @Autowired
    public GerenteService(GerenteRepository gerenteRepository) {
        this.gerenteRepository = gerenteRepository;
    }

    public List<GerenteDTO> getGerentes() {
        return gerenteRepository.getGerentes();
    }

    public boolean existeGerenteComCpf(String cpf) {
        return gerenteRepository.existeGerenteComCpf(cpf);
    }

    @Transactional
    public GerenteDTO adicionarGerente(GerenteDTO gerenteDTO) {
        try {
            Long idGerente = gerenteRepository.adicionarGerente(gerenteDTO);
            gerenteDTO.setId(idGerente);

            return gerenteDTO;
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao adicionar gerente");
        }
    }

    public GerenteDTO alterarGerente(GerenteDTO gerenteDTO)
    {
        try 
        {
            return gerenteRepository.alterarGerente(gerenteDTO);
        } 
        catch (Exception e) 
        {
            throw new RuntimeException("Erro ao alterar gerente");
        }
    }

    public void deletarGerente(GerenteDTO gerenteDTO)
    {
        gerenteRepository.deletarGerente(gerenteDTO);
        
        return;
    }
}
