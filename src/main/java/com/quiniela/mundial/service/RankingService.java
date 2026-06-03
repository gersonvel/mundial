package com.quiniela.mundial.service;

import com.quiniela.mundial.dto.RankingDesempateDTO;
import com.quiniela.mundial.model.Usuario;
import java.util.List;

public interface RankingService {
    List<Usuario> obtenerRanking();

    public List<RankingDesempateDTO> obtenerRankingConCriterios();
}