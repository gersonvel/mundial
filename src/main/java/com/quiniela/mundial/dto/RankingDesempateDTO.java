package com.quiniela.mundial.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RankingDesempateDTO {

    private String username;
    private Long puntosTotales;
    private Long cincos;
    private Long cuatros;
    private Long tres;
    private Long unos;

    public RankingDesempateDTO(String username, Long puntosTotales, Long cincos, Long cuatros, Long tres, Long unos) {
        this.username = username;
        this.puntosTotales = puntosTotales != null ? puntosTotales : 0L;
        this.cincos = cincos != null ? cincos : 0L;
        this.cuatros = cuatros != null ? cuatros : 0L;
        this.tres = tres != null ? tres : 0L;
        this.unos = unos != null ? unos : 0L;
    }
}