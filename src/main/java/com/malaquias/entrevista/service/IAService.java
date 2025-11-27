package com.malaquias.entrevista.service;

import com.malaquias.entrevista.model.IaCache;
import com.malaquias.entrevista.repository.IaCacheRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Optional;

@Service
public class IAService {
    private final IaCacheRepository cacheRepo;
    private final WebClient webClient;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${app.openai.model:gpt-3.5-turbo}")
    private String modelo;

    // Toma API key desde variable de entorno OPENAI_API_KEY
    public IAService(IaCacheRepository cacheRepo) {
        this.cacheRepo = cacheRepo;
        String apiKey = System.getenv("OPENAI_API_KEY");
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    // Calcula hash simple del prompt
    private String sha256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String preguntarIA(String pregunta) {
        String prompt = buildPrompt(pregunta);
        String hash = sha256(prompt);

        Optional<IaCache> cached = cacheRepo.findByPromptHash(hash);
        if (cached.isPresent()) {
            return cached.get().getRespuestaTexto();
        }

        // Preparar payload para Chat Completions (gpt-3.5-turbo style)
        try {
            String body = mapper.writeValueAsString(
                java.util.Map.of(
                    "model", modelo,
                    "messages", java.util.List.of(
                        java.util.Map.of("role","system","content","Eres un asistente conciso para respuestas técnicas."),
                        java.util.Map.of("role","user","content", prompt)
                    ),
                    "max_tokens", 500,
                    "temperature", 0.2
                )
            );

            Mono<String> respMono = webClient.post()
                    .uri("/chat/completions")
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class);

            String respStr = respMono.block(); // bloquear en este servicio simple
            JsonNode node = mapper.readTree(respStr);
            String texto = node.get("choices").get(0).get("message").get("content").asText();

            // Guardar en cache
            IaCache c = new IaCache();
            c.setPromptHash(hash);
            c.setPreguntaTexto(pregunta);
            c.setRespuestaTexto(texto);
            c.setModelo(modelo);
            // tokensUsados si el api devuelve usage -> parsearlo
            cacheRepo.save(c);

            return texto;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: no se pudo obtener respuesta de la IA.";
        }
    }

    private String buildPrompt(String pregunta) {
        // Plantilla: pide respuesta corta + ejemplo si aplica
        return "Responder brevemente (1-2 párrafos) a la siguiente pregunta técnica. Si es útil, incluye un pequeño ejemplo de código.\n\nPregunta: " + pregunta + "\n\nRespuesta:";
    }
}