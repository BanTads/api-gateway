package com.apigateway.gerente.gerente.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GerenteDashboardDTO {
    private Long id;
    private String nome;
    private int numeroClientes;
    private double saldoPositivoTotal;
    private double saldoNegativoTotal;

    // Getters and Setters
}