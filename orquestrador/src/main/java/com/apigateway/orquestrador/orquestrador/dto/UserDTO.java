package com.apigateway.orquestrador.orquestrador.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private String nome;
    @NotBlank
    private String senha;
    @NotBlank
    private String salt;
    @NotBlank
    private String cargo;
    @NotBlank
    @Email
    private String email;
}
