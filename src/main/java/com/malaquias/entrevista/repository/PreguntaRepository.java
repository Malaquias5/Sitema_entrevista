package com.malaquias.entrevista.repository;

import com.malaquias.entrevista.*;
import com.malaquias.entrevista.model.Pregunta;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {
    List<Pregunta> findByTituloContainingIgnoreCaseOrContenidoContainingIgnoreCase(String t, String c);
}