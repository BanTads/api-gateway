package com.apigateway.conta.conta.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ExtratoDTO {
    private Date dataHora;
    private String operacao;
    private Long idCliente;
    private String nomeCliente;
    private Double valor;
    private String tipo;
    private Double saldoConsolidado;
}
