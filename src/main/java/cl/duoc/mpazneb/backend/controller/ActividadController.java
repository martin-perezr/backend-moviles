package cl.duoc.mpazneb.backend.controller;

import cl.duoc.mpazneb.backend.dto.ActividadDto;
import cl.duoc.mpazneb.backend.dto.ActividadRequest;
import cl.duoc.mpazneb.backend.repository.ActividadRepositorio;
import cl.duoc.mpazneb.backend.validation.ActividadValidador;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/actividades")
public class ActividadController {
    private final ActividadRepositorio repositorio;

    public ActividadController(ActividadRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @GetMapping
    public List<ActividadDto> listar() {
        return repositorio.listar();
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ActividadRequest solicitud) {
        String error = ActividadValidador.validar(solicitud);
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("error", error));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(repositorio.crear(solicitud));
    }
}