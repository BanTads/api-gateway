package com.apigateway.gerente.gerente.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class ContaDTO implements Serializable {
    private Long numeroConta;
    private Boolean aprovada;
    private Long idCliente;
    private Date dataCriacao;
    private double limite;
    private Long idGerente;
    private SaldoLimiteDTO saldo;
}