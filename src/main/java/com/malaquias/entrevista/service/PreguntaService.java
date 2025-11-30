package com.malaquias.entrevista.service;

import com.malaquias.entrevista.model.Pregunta;
import com.malaquias.entrevista.repository.PreguntaRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;

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

    // Nueva búsqueda por inicial de categoría (B, J, S, A, T, M)
    public List<Pregunta> buscarPorInicialCategoria(String inicial) {
        if (inicial == null || inicial.isBlank()) return repo.findAll();
        return repo.findByCategoriaStartingWithIgnoreCase(inicial);
    }

    // Guardar nueva pregunta manual
    public Pregunta guardar(Pregunta p) {
        return repo.save(p);
    }

    // Obtener por ID
    public Pregunta obtenerPorId(Long id) {
        return repo.findById(id).orElseThrow(() -> new NoSuchElementException("Pregunta no encontrada: " + id));
    }

    // Actualizar pregunta completa (incluye respuesta)
    public Pregunta actualizar(Long id, Pregunta data) {
        Pregunta p = obtenerPorId(id);
        p.setTitulo(data.getTitulo());
        p.setContenido(data.getContenido());
        p.setCategoria(data.getCategoria());
        p.setEtiquetas(data.getEtiquetas());
        p.setRespuesta(data.getRespuesta());
        return repo.save(p);
    }
}