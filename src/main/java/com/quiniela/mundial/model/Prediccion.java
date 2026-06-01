package com.quiniela.mundial.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "predicciones", uniqueConstraints = { @UniqueConstraint(columnNames = { "usuario_id", "partido_id" }) })
@Data
public class Prediccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "partido_id", nullable = false)
    private Partido partido;

    @Column(name = "goles_local_pred", nullable = false)
    private int golesLocalPred;

    @Column(name = "goles_visitante_pred", nullable = false)
    private int golesVisitantePred;

    @Column(name = "puntos_ganados")
    private Integer puntosGanados;
}
