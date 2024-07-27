package com.apigateway.cliente.cliente.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClienteDTO {
    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private String telefone;
    private Float salario;
    private EnderecoDTO endereco;

    public ClienteDTO() {}
    public ClienteDTO(Long id, String nome, String email, String cpf, String telefone, Float salario, EnderecoDTO endereco) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
        this.telefone = telefone;
        this.salario = salario;
        this.endereco = endereco;
    }
}