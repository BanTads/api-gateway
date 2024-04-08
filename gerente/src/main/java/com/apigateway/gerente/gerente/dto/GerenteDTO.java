package com.apigateway.gerente.gerente.dto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class GerenteDTO implements Serializable
{
    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private String telefone;
}