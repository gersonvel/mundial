package com.quiniela.mundial.service;

import java.util.List;

import com.quiniela.mundial.dto.PrediccionRequestDTO;
import com.quiniela.mundial.model.Prediccion;

public interface PrediccionService {
    Prediccion guardarPrediccion(Long usuarioId, PrediccionRequestDTO request);

    List<Prediccion> obtenerPrediccionesPorUsuario(Long usuarioId);
}
