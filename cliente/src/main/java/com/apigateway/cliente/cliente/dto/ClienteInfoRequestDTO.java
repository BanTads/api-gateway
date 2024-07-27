package com.apigateway.cliente.cliente.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ClienteInfoRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long clienteId;

    public ClienteInfoRequestDTO() {
    }

    // Construtor com parâmetros
    public ClienteInfoRequestDTO(Long clienteId) {
        this.clienteId = clienteId;
    }
}