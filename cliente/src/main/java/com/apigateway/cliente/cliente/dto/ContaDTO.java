package com.apigateway.cliente.cliente.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
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
    private String motivo;
    private SaldoLimiteDTO saldo;

}