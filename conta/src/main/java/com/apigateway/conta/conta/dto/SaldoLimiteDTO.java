package com.apigateway.conta.conta.dto;

import com.apigateway.conta.conta.model.Conta;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SaldoLimiteDTO implements Serializable {
    private double saldo;
    private double limite;
    private double total;
    private Conta conta; // não obrigatório

    public SaldoLimiteDTO(Double saldo, Double limite, Double total) {
        this.saldo = saldo;
        this.limite = limite;
        this.total = total;
    }

    public SaldoLimiteDTO(Double saldo, Double limite, Double total, Conta conta) {
        this.saldo = saldo;
        this.limite = limite;
        this.total = total;
        this.conta = conta;
    }

}
