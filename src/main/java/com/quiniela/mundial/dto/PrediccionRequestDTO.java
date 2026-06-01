package com.quiniela.mundial.dto;

import lombok.Data;

@Data
public class PrediccionRequestDTO {
    private Long partidoId;
    private int golesLocalPred;
    private int golesVisitantePred;
}
