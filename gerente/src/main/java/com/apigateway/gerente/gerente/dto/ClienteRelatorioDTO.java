package com.apigateway.gerente.gerente.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClienteRelatorioDTO {
    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private String telefone;
    private float salario;
    private ContaDTO conta;
    private EnderecoDTO endereco;
}
