package com.malaquias.entrevista.repository;

import com.malaquias.entrevista.model.IaCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IaCacheRepository extends JpaRepository<IaCache, Long> {
    Optional<IaCache> findByPromptHash(String promptHash);
}