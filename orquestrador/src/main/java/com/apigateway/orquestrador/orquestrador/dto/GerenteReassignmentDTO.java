package com.apigateway.orquestrador.orquestrador.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GerenteReassignmentDTO {
    private Long oldGerenteId;
    private Long newGerenteId;
    private boolean gerenteCriado;
}