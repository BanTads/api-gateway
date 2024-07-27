package com.apigateway.conta.conta.dto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExtratoResponseDTO {
    private List<ExtratoDTO> extrato;
    private Double saldoFinal;
}
