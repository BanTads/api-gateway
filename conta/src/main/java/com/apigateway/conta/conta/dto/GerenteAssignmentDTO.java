package com.apigateway.conta.conta.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GerenteAssignmentDTO {
    private Long gerenteId;
    private Long numeroConta;
}