package com.apigateway.conta.conta.repositories;

import com.apigateway.conta.conta.model.Conta;
import com.apigateway.conta.conta.model.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    @Query("SELECT COALESCE(" +
            "SUM(CASE WHEN m.tipo = 'DEPOSITO' THEN m.valor ELSE 0 END) + " +
            "SUM(CASE WHEN m.tipo = 'TRANSFERENCIA' AND m.idContaDestino = :idConta THEN m.valor ELSE 0 END) - " +
            "SUM(CASE WHEN m.tipo = 'SAQUE' THEN m.valor ELSE 0 END) - " +
            "SUM(CASE WHEN m.tipo = 'TRANSFERENCIA' AND m.idContaOrigem = :idConta THEN m.valor ELSE 0 END), 0.00) AS saldo " +
            "FROM Movimentacao m " +
            "WHERE m.idContaOrigem = :idConta OR m.idContaDestino = :idConta")
    Double calcularSaldo(@Param("idConta") Long idConta);

    @Query("SELECT m FROM Movimentacao m WHERE (m.idContaOrigem = :idConta OR m.idContaDestino = :idConta) AND m.dataHora BETWEEN :dataInicio AND :dataFim ORDER BY m.dataHora ASC")
    List<Movimentacao> findMovimentacoesByDateRangeAndConta(@Param("idConta") Long idConta, @Param("dataInicio") Date dataInicio, @Param("dataFim") Date dataFim);
}