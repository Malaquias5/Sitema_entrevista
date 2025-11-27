package com.malaquias.entrevista.repository;

import com.malaquias.entrevista.model.Pregunta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {
    List<Pregunta> findByTituloContainingIgnoreCaseOrContenidoContainingIgnoreCase(String t, String c);
}