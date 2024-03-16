package com.apigateway.cliente.cliente.dto;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ClienteDTO {
    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private String telefone;
    private float salario;
    private EnderecoDTO endereco;
}
