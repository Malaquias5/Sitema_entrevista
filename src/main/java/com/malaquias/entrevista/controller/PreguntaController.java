package com.malaquias.entrevista.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.malaquias.entrevista.model.Pregunta;
import com.malaquias.entrevista.service.PreguntaService;

@RestController
@RequestMapping("/api/preguntas")
@CrossOrigin(origins = "http://localhost:4200")
public class PreguntaController {

    private final PreguntaService service;

    public PreguntaController(PreguntaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Pregunta> listar() { return service.listar(); }

    @GetMapping("/search")
    public List<Pregunta> buscar(@RequestParam String keyword) {
        return service.buscar(keyword);
    }

    @PostMapping
    public Pregunta crear(@RequestBody Pregunta pregunta){
        return service.guardar(pregunta);
    }
}

