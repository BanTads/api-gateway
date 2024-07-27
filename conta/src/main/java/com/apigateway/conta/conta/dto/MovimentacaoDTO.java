package com.apigateway.conta.conta.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class MovimentacaoDTO implements Serializable {
    private Long id;
    private Date dataHora;
    private TipoMovimentacao tipo;
    private double valor;
    private Long idContaOrigem;
    private Long idContaDestino;
}
