package com.apigateway.conta.conta.repositories;

import com.apigateway.conta.conta.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContaRepository extends JpaRepository<Conta, Long> {

}
