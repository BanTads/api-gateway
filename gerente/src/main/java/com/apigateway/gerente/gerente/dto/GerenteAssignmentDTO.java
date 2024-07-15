package com.apigateway.gerente.gerente.dto;

import lombok.*;

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