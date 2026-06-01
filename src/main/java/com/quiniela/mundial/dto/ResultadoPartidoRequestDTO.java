package com.quiniela.mundial.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoPartidoRequestDTO {
    private int golesLocal;
    private int golesVisitante;
    private String ganadorPenales;
}