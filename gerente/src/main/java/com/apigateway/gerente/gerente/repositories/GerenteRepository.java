package com.apigateway.gerente.gerente.repositories;

import com.apigateway.gerente.gerente.model.Gerente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GerenteRepository extends JpaRepository<Gerente, Long> {
    public Gerente findByEmail(String email);
}
