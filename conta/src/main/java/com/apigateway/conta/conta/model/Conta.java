package com.apigateway.conta.conta.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="Contas")
public class Conta implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name="id_cliente", unique = true)
    private Long idCliente;

    @Column(name="aprovada")
    private Boolean aprovada;

    @Column(name="data_criacao")
    private Date dataCriacao;

    @Column(name="limite")
    private double limite;

    @Column(name="id_gerente")
    private Long idGerente;

    public Conta() {
        super();
    }
    public Conta(Long id, Long idCliente, Boolean aprovada, Date dataCriacao, double limite, Long idGerente) {
        this.id = id;
        this.idCliente = idCliente;
        this.aprovada = aprovada;
        this.dataCriacao = dataCriacao;
        this.limite = limite;
        this.idGerente = idGerente;
    }

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public Boolean getAprovada() {
        return aprovada;
    }

    public void setAprovada(Boolean aprovada) {
        this.aprovada = aprovada;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public double getLimite() {
        return limite;
    }

    public void setLimite(double limite) {
        this.limite = limite;
    }

    public Long getIdGerente() {
        return idGerente;
    }

    public void setIdGerente(Long idGerente) {
        this.idGerente = idGerente;
    }
}