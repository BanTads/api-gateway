package com.apigateway.orquestrador.orquestrador.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserChangeDTO {
    private String oldEmail;
    private String newEmail;
}
