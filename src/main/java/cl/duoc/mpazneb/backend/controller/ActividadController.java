package cl.duoc.mpazneb.backend.controller;

import cl.duoc.mpazneb.backend.dto.ActividadDto;
import cl.duoc.mpazneb.backend.dto.ActividadRequest;
import cl.duoc.mpazneb.backend.repository.ActividadRepositorio;
import cl.duoc.mpazneb.backend.validation.ActividadValidador;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable long id,
                                    @RequestBody ActividadRequest solicitud) {
        if (repositorio.buscarPorId(id).isEmpty()) {
            return noEncontrada();
        }
        String error = ActividadValidador.validar(solicitud);
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("error", error));
        }
        repositorio.actualizar(id, solicitud);
        return ResponseEntity.ok(repositorio.buscarPorId(id).orElseThrow());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable long id) {
        Optional<ActividadDto> actual = repositorio.buscarPorId(id);
        if (actual.isEmpty()) {
            return noEncontrada();
        }
        if (repositorio.eliminarBorradorLibre(id) == 1) {
            return ResponseEntity.noContent().build();
        }
        String motivo = "BORRADOR".equals(actual.get().estado())
            ? "La actividad tiene asignaciones y no se puede eliminar"
            : "Solo se pueden eliminar actividades en borrador";
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", motivo));
    }

    private ResponseEntity<?> noEncontrada() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", "La actividad no existe"));
    }
}