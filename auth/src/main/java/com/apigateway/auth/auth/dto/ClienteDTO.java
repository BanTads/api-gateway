package com.apigateway.auth.auth.dto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ClienteDTO implements Serializable {
    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private String telefone;
    private float salario;
}