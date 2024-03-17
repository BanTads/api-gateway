package com.apigateway.cliente.cliente.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnderecoDTO {
    private Long id;
    private String tipo;
    private String logradouro;
    private String numero;
    private String complemento;
    private String cep;
    private String cidade;
    private String uf;
}
