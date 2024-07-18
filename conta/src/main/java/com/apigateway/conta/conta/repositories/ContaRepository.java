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

    @Query("SELECT c FROM Conta c WHERE c.idGerente = :idGerente ORDER BY (SELECT COALESCE(SUM(CASE WHEN m.tipo = 'DEPOSITO' THEN m.valor ELSE 0 END) + SUM(CASE WHEN m.tipo = 'TRANSFERENCIA' AND m.idContaDestino = c.numeroConta THEN m.valor ELSE 0 END) - SUM(CASE WHEN m.tipo = 'SAQUE' THEN m.valor ELSE 0 END) - SUM(CASE WHEN m.tipo = 'TRANSFERENCIA' AND m.idContaOrigem = c.numeroConta THEN m.valor ELSE 0 END), 0.0) FROM Movimentacao m WHERE m.idContaOrigem = c.numeroConta OR m.idContaDestino = c.numeroConta) DESC")
    List<Conta> findTop3ContasByGerente(Long idGerente);
}
