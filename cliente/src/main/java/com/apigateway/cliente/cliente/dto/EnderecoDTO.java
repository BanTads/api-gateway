package com.apigateway.cliente.cliente.dto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

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

    public EnderecoDTO() {}

    public EnderecoDTO(Long id, String tipo, String logradouro, String numero, String complemento, String cep, String cidade, String uf) {
        this.id = id;
        this.tipo = tipo;
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.cep = cep;
        this.cidade = cidade;
        this.uf = uf;
    }
}