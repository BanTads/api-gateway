package com.apigateway.orquestrador.orquestrador.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class ContaDTO implements Serializable {
    private Long numeroConta;
    private Boolean aprovada;
    private String motivo;
    private Long idCliente;
    private Date dataCriacao;
    private double limite;
    private Long idGerente;
}