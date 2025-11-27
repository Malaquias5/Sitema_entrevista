package com.malaquias.entrevista.service;

import com.malaquias.entrevista.model.Pregunta;
import com.malaquias.entrevista.repository.PreguntaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PreguntaService {
    private final PreguntaRepository repo;

    public PreguntaService(PreguntaRepository repo) {
        this.repo = repo;
    }

    // Listar todas las preguntas de la DB
    public List<Pregunta> listar() {
        return repo.findAll();
    }

    // Buscar por palabra clave en título o contenido
    public List<Pregunta> buscar(String keyword) {
        if (keyword == null || keyword.isBlank()) return repo.findAll();
        return repo.findByTituloContainingIgnoreCaseOrContenidoContainingIgnoreCase(keyword, keyword);
    }

    // Guardar nueva pregunta manual
    public Pregunta guardar(Pregunta p) {
        return repo.save(p);
    }
}