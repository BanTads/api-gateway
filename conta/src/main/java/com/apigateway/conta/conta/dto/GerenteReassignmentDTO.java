package com.apigateway.conta.conta.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GerenteReassignmentDTO {
    private Long oldGerenteId;
    private Long newGerenteId;
    private boolean gerenteCriado;

    public boolean getGerenteCriado() {
        return gerenteCriado;
    }
}