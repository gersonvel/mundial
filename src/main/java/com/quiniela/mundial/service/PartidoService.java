package com.quiniela.mundial.service;

import com.quiniela.mundial.model.Partido;
import java.util.List;

public interface PartidoService {
    List<Partido> obtenerPartidosActivos();

    Partido registrarResultado(Long partidoId, int golesLocal, int golesVisitante, String ganadorPenales);

    Partido guardarPartido(Partido partido);

    List<Partido> obtenerTodos();

    void eliminarPorId(Long partidoId);
}