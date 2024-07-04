package com.apigateway.gerente.gerente.model;
import jakarta.persistence.*;
@Entity
@Table(name="gerentes")
public class Gerente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name="nome", nullable = false)
    private String nome;

    @Column(name="telefone", nullable = false)
    private String telefone;

    @Column(name="email", unique = true, nullable = false)
    private String email;

    @Column(name="cpf", unique = true, nullable = false)
    private String cpf;

    @Column(name="quantidade_contas", unique = true, nullable = false)
    private int quantidadeContas;

    public Gerente() {
        super();
    }

    public Gerente(long id, String nome, String email, String cpf, String telefone, int quantidadeContas) {
        super();
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
        this.telefone = telefone;
        this.quantidadeContas = quantidadeContas;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public int getQuantidadeContas() {
        return quantidadeContas;
    }
    public void setQuantidadeContas(int quantidadeContas) {
        this.quantidadeContas = quantidadeContas;
    }
}
