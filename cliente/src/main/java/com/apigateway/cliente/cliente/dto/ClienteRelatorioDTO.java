package com.apigateway.cliente.cliente.dto;

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
    private EnderecoDTO endereco;
    private ContaDTO conta;
    private GerenteDTO gerente;
}
