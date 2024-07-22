package com.apigateway.orquestrador.orquestrador.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SaldoLimiteDTO implements Serializable {
    private double saldo;
    private double limite;
    private double total;

    public SaldoLimiteDTO(Double saldo, Double limite, Double total) {
        this.saldo = saldo;
        this.limite = limite;
        this.total = total;
    }
}
