package com.malaquias.entrevista.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.malaquias.entrevista.model.IaCache;
import com.malaquias.entrevista.repository.IaCacheRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.annotation.PostConstruct;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Optional;

@Service
public class IAService {

    private final IaCacheRepository cacheRepo;
    private final WebClient webClient;
    private final ObjectMapper mapper = new ObjectMapper();

    // ----------- CONFIGURACIONES DESDE application.properties -----------

    @Value("${app.openai.apiKey}")  // ✔ coincide con el .properties
    private String apiKey;

    @Value("${app.openai.model:gpt-4o-mini}")  // ✔ modelo por defecto
    private String modelo;

    // --------------------------------------------------------------------

    public IAService(IaCacheRepository cacheRepo) {
        this.cacheRepo = cacheRepo;

        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();
    }

    // Validar que la API KEY exista
    @PostConstruct
    public void validarApiKey() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new RuntimeException("❌ ERROR: No se encontró app.openai.apiKey en application.properties");
        }
    }


    // Crear hash SHA-256 del prompt para caching
    private String sha256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));

            return sb.toString();
        } catch (Exception e) {
            return String.valueOf(text.hashCode());
        }
    }

    // -------------------- MÉTODO PRINCIPAL --------------------------------

    public String preguntarIA(String pregunta) {
        String hash = sha256(pregunta);

        Optional<IaCache> cache = cacheRepo.findByPromptHash(hash);
        if (cache.isPresent()) {
            return cache.get().getRespuestaTexto();
        }

        try {
            String body = """
                {
                  "model": "%s",
                  "messages": [
                    { "role": "system", "content": "Eres un asistente técnico conciso." },
                    { "role": "user", "content": "%s" }
                  ]
                }
                """.formatted(modelo, pregunta);

            String responseJson = webClient.post()
                    .uri("/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = mapper.readTree(responseJson);
            String texto = root.path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

            IaCache record = new IaCache();
            record.setPromptHash(hash);
            record.setPreguntaTexto(pregunta);
            record.setRespuestaTexto(texto);
            record.setModelo(modelo);
            cacheRepo.save(record);

            return texto;

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error: no se pudo obtener respuesta de la IA.";
        }
    }



    // Prompt pre-diseñado
    private String buildPrompt(String pregunta) {
        return """
                Responde en 1–2 párrafos la siguiente pregunta técnica.
                Incluye un pequeño ejemplo de código si es necesario.

                Pregunta: %s

                Respuesta:
                """.formatted(pregunta);
    }

    public String generarRespuesta(String pregunta) {
        return preguntarIA(pregunta);
    }
}
