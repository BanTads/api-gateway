package com.apigateway.auth.auth.model;

import com.apigateway.auth.auth.dto.ClienteDTO;
import com.apigateway.auth.auth.dto.GerenteDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document("usuarios")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User implements Serializable {
    @Id
    public String id;

    public String nome;
    public String email;
    public String senha;
    public String salt;
    public String cargo;
    public GerenteDTO gerente;
    public ClienteDTO cliente;

    public User() {}

    public User(String nome, String email, String senha, String cargo, String salt, GerenteDTO gerenteDTO, ClienteDTO clienteDTO) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.salt = salt;
        this.cargo = cargo;
        this.gerente = gerenteDTO;
        this.cliente = clienteDTO;
    }

    @Override
    public String toString() {
        return String.format(
                "UserModel[id=%s, nome='%s', email='%s', senha='%s', cargo='%s', salt='%s']",
                id, nome, email, senha, cargo, salt);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public GerenteDTO getGerente() {
        return gerente;
    }

    public void setGerente(GerenteDTO gerenteDTO) {
        this.gerente = gerenteDTO;
    }

    public ClienteDTO getCliente() {
        return cliente;
    }

    public void setCliente(ClienteDTO clienteDTO) {
        this.cliente = clienteDTO;
    }
}
