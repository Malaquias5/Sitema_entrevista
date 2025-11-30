package com.malaquias.entrevista.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/by-categoria")
    public List<Pregunta> buscarPorInicial(@RequestParam String inicial) {
        return service.buscarPorInicialCategoria(inicial);
    }

    // Obtener por ID
    @GetMapping("/{id}")
    public Pregunta obtener(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    // Crear
    @PostMapping
    public Pregunta crear(@RequestBody Pregunta pregunta){
        return service.guardar(pregunta);
    }

    // Actualizar completa (incluye respuesta)
    @PutMapping("/{id}")
    public Pregunta actualizar(@PathVariable Long id, @RequestBody Pregunta pregunta) {
        return service.actualizar(id, pregunta);
    }
}