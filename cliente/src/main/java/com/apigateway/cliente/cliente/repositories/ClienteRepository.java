package com.apigateway.cliente.cliente.repositories;

import com.apigateway.cliente.cliente.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

}
