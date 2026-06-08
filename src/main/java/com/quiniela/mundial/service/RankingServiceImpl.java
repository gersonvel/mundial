package com.quiniela.mundial.service;

import com.quiniela.mundial.dto.RankingDesempateDTO;
import com.quiniela.mundial.model.Usuario;
import com.quiniela.mundial.repository.PrediccionRepository;
import com.quiniela.mundial.repository.UsuarioRepository;
// import com.quiniela.mundial.service.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RankingServiceImpl implements RankingService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PrediccionRepository prediccionRepository;

    @Override
    public List<Usuario> obtenerRanking() {
        return usuarioRepository.findAllByOrderByPuntosTotalesDesc();
    }

    @Override
    public List<RankingDesempateDTO> obtenerRankingConCriterios() {
        List<RankingDesempateDTO> datosRaw = prediccionRepository.obtenerEstadisticasDesempateGlobal();

        Comparator<RankingDesempateDTO> comparadorDesempate = Comparator
                .comparingLong(RankingDesempateDTO::getPuntosTotales).reversed()
                .thenComparing(Comparator.comparingLong(RankingDesempateDTO::getCincos).reversed())
                .thenComparing(Comparator.comparingLong(RankingDesempateDTO::getCuatros).reversed())
                .thenComparing(Comparator.comparingLong(RankingDesempateDTO::getTres).reversed())
                .thenComparing(Comparator.comparingLong(RankingDesempateDTO::getDos).reversed())
                .thenComparing(Comparator.comparingLong(RankingDesempateDTO::getUnos).reversed());

        return datosRaw.stream()
                .sorted(comparadorDesempate)
                // .peek(user -> System.out.println("Usuario: " +
                // user.getUsername() + " | Pts: "
                // + user.getPuntosTotales() + " | Cincos: " + user.getCincos()))
                .collect(Collectors.toList());
    }
}