package com.apigateway.gerente.gerente.repositories;

import com.apigateway.gerente.gerente.model.Gerente;
import com.apigateway.gerente.gerente.dto.GerenteDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GerenteRepository extends JpaRepository<Gerente, Long> {
    List<GerenteDTO> getGerentes();

    GerenteDTO getGerenteByCPF(String cpf);

    long adicionarGerente(GerenteDTO gerenteDTO);

    GerenteDTO alterarGerente(GerenteDTO gerenteDTO);

    void deletarGerente(GerenteDTO gerenteDTO);

    boolean existeGerenteComCpf(String cpf);
}