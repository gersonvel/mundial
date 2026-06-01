package com.quiniela.mundial.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "partidos")
@Data
public class Partido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "equipo_local", nullable = false)
    private String equipoLocal;

    @Column(name = "equipo_visitante", nullable = false)
    private String equipoVisitante;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "goles_local")
    private Integer golesLocal;

    @Column(name = "goles_visitante")
    private Integer golesVisitante;

    @Column(nullable = false)
    private String estado = "PENDIENTE"; // "PENDIENTE" o "JUGADO"

    @Column(name = "grupo") // DEPUES DE FASE DE GRUPOS SERA NULL
    private String grupo;

    @Column(name = "fase", nullable = false) // "JORNADA_1"..., "OCTAVOS", "CUARTOS", "SEMIS", "FINAL"
    private String fase;

    @Column(name = "ganador_penales")
    private String ganadorPenales;

    @Column(name = "bandera_local")
    private String banderaLocal;

    @Column(name = "bandera_visitante")
    private String banderaVisitante;
}
