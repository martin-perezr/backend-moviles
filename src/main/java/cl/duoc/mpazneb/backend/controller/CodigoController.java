package cl.duoc.mpazneb.backend.controller;

import cl.duoc.mpazneb.backend.dto.AsignacionDto;
import cl.duoc.mpazneb.backend.dto.ValidarCodigoRequest;
import cl.duoc.mpazneb.backend.service.AsignacionServicio;
import cl.duoc.mpazneb.backend.service.RespuestaServicio;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/codigos")
public class CodigoController {
    private final AsignacionServicio servicio;

    public CodigoController(AsignacionServicio servicio) {
        this.servicio = servicio;
    }

    @PostMapping("/validar")
    public ResponseEntity<?> validar(@RequestBody ValidarCodigoRequest solicitud) {
        RespuestaServicio<AsignacionDto> resultado = servicio.validarCodigo(solicitud);
        if (resultado.esError()) {
            return ResponseEntity.status(resultado.estado())
                .body(Map.of("error", resultado.error()));
        }
        return ResponseEntity.ok(resultado.cuerpo());
    }
}