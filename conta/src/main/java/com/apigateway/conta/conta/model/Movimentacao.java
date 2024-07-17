package com.apigateway.conta.conta.model;

import com.apigateway.conta.conta.dto.TipoMovimentacao;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="movimentacao")
public class Movimentacao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="data_hora", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataHora;

    @Enumerated(EnumType.STRING)
    @Column(name="tipo", nullable = false)
    private TipoMovimentacao tipo;

    @Column(name="valor", nullable = false)
    private double valor;

    @Column(name = "id_conta_origem")
    private Long idContaOrigem;

    @Column(name = "id_conta_destino")
    private Long idContaDestino;

    public Movimentacao() {
        super();
    }

    public Movimentacao(Date dataHora, TipoMovimentacao tipo, double valor, Long idContaOrigem, Long idContaDestino) {
        this.dataHora = dataHora;
        this.tipo = tipo;
        this.valor = valor;
        this.idContaOrigem = idContaOrigem;
        this.idContaDestino = idContaDestino;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataHora() {
        return dataHora;
    }

    public void setDataHora(Date dataHora) {
        this.dataHora = dataHora;
    }

    public TipoMovimentacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentacao tipo) {
        this.tipo = tipo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public Long getIdContaOrigem() {
        return idContaOrigem;
    }

    public void setIdContaOrigem(Long idContaOrigem) {
        this.idContaOrigem = idContaOrigem;
    }

    public Long getIdContaDestino() {
        return idContaDestino;
    }

    public void setIdContaDestino(Long idContaDestino) {
        this.idContaDestino = idContaDestino;
    }
}