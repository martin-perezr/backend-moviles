package cl.duoc.mpazneb.backend.controller;

import cl.duoc.mpazneb.backend.dto.AsignacionDto;
import cl.duoc.mpazneb.backend.dto.AsignacionRequest;
import cl.duoc.mpazneb.backend.service.AsignacionServicio;
import cl.duoc.mpazneb.backend.service.RespuestaServicio;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/asignaciones")
public class AsignacionController {
    private final AsignacionServicio servicio;

    public AsignacionController(AsignacionServicio servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody AsignacionRequest solicitud) {
        RespuestaServicio<AsignacionDto> resultado = servicio.crear(solicitud);
        if (resultado.esError()) {
            return ResponseEntity.status(resultado.estado())
                .body(Map.of("error", resultado.error()));
        }
        return ResponseEntity.status(resultado.estado()).body(resultado.cuerpo());
    }
}