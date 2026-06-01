package com.quiniela.mundial.service;

import com.quiniela.mundial.model.Usuario;
import com.quiniela.mundial.repository.UsuarioRepository;
// import com.quiniela.mundial.service.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RankingServiceImpl implements RankingService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public List<Usuario> obtenerRanking() {
        return usuarioRepository.findAllByOrderByPuntosTotalesDesc();
    }
}