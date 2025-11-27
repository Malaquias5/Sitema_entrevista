package com.malaquias.entrevista.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "ia_cache")
public class IaCache {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 128, unique = true)
    private String promptHash;

    @Column(columnDefinition = "TEXT")
    private String preguntaTexto;

    @Column(columnDefinition = "TEXT")
    private String respuestaTexto;

    private String modelo;
    private Integer tokensUsados;
    private Instant createdAt = Instant.now();

    // getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPromptHash() { return promptHash; }
    public void setPromptHash(String promptHash) { this.promptHash = promptHash; }

    public String getPreguntaTexto() { return preguntaTexto; }
    public void setPreguntaTexto(String preguntaTexto) { this.preguntaTexto = preguntaTexto; }

    public String getRespuestaTexto() { return respuestaTexto; }
    public void setRespuestaTexto(String respuestaTexto) { this.respuestaTexto = respuestaTexto; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public Integer getTokensUsados() { return tokensUsados; }
    public void setTokensUsados(Integer tokensUsados) { this.tokensUsados = tokensUsados; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}