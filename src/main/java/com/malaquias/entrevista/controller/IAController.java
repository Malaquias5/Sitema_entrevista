package com.malaquias.entrevista.controller;

import com.malaquias.entrevista.service.IAService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ia")
@CrossOrigin(origins = "http://localhost:4200") // Angular
public class IAController {

    private final IAService iaService;

    public IAController(IAService iaService) {
        this.iaService = iaService;
    }

    // Endpoint principal para preguntar
    @PostMapping("/preguntar")
    public String preguntar(@RequestBody Map<String, String> body) {
        String pregunta = body.get("texto");
        return iaService.generarRespuesta(pregunta);
    }
}
