package com.apigateway.cliente.cliente.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GerenteDTO {
    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private String telefone;
    private int quantidadeContas;
}
