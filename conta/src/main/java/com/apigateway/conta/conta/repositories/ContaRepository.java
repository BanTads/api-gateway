package com.apigateway.conta.conta.repositories;

import com.apigateway.conta.conta.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContaRepository extends JpaRepository<Conta, Long> {
    List<Conta> findByIdGerente(Long idGerente);
    Conta findByIdCliente(Long idCliente);
    @Query("SELECT c FROM Conta c WHERE c.idGerente = :idGerente AND c.aprovada IS NULL")
    List<Conta> findByIdGerenteAndAprovadaIsNull(Long idGerente);
}
