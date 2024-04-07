package com.apigateway.cliente.cliente.repositories;

import com.apigateway.cliente.cliente.model.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
}
