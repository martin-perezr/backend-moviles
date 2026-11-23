package cl.duoc.mpazneb.backend.service;

import cl.duoc.mpazneb.backend.dto.AsignacionDto;
import cl.duoc.mpazneb.backend.dto.AsignacionRequest;
import cl.duoc.mpazneb.backend.dto.ValidarCodigoRequest;
import cl.duoc.mpazneb.backend.repository.ActividadRepositorio;
import cl.duoc.mpazneb.backend.repository.AsignacionRepositorio;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AsignacionServicio {
    public static final long DURACION_MS = 30L * 60L * 1000L;
    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_FINALIZADA = "FINALIZADA";
    private static final String ESTADO_DISPONIBLE = "DISPONIBLE";

    private final AsignacionRepositorio asignaciones;
    private final ActividadRepositorio actividades;
    private final GeneradorCodigoTemporal generador;
    private final Reloj reloj;

    public AsignacionServicio(
            AsignacionRepositorio asignaciones,
            ActividadRepositorio actividades,
            GeneradorCodigoTemporal generador,
            Reloj reloj) {
        this.asignaciones = asignaciones;
        this.actividades = actividades;
        this.generador = generador;
        this.reloj = reloj;
    }

    @Transactional
    public RespuestaServicio<AsignacionDto> crear(AsignacionRequest solicitud) {
        if (solicitud == null
                || solicitud.estudianteId() == null
                || solicitud.actividadId() == null
                || solicitud.asistenteId() == null) {
            return RespuestaServicio.error(HttpStatus.BAD_REQUEST, "Faltan datos de la asignación");
        }
        long estudianteId = solicitud.estudianteId();
        long actividadId = solicitud.actividadId();
        long asistenteId = solicitud.asistenteId();

        if (estudianteId <= 0 || actividadId <= 0 || asistenteId <= 0) {
            return RespuestaServicio.error(HttpStatus.BAD_REQUEST, "Los identificadores deben ser positivos");
        }

        var actividad = actividades.buscarPorId(actividadId);
        if (actividad.isEmpty()) {
            return RespuestaServicio.error(HttpStatus.NOT_FOUND, "La actividad no existe");
        }
        if (!ESTADO_DISPONIBLE.equals(actividad.get().estado())) {
            return RespuestaServicio.error(
                HttpStatus.BAD_REQUEST, "La actividad no está disponible para asignar");
        }

        long ahora = reloj.ahora();
        if (asignaciones.estudianteConSesionAbierta(estudianteId, ahora)) {
            return RespuestaServicio.error(
                HttpStatus.CONFLICT, "Ese estudiante ya tiene una actividad abierta");
        }

        String codigo = generador.generarUnico(c -> asignaciones.codigoVigenteEnUso(c, ahora));
        long venceEn = ahora + DURACION_MS;
        AsignacionDto guardada = asignaciones.insertar(
            estudianteId, actividadId, asistenteId, codigo, ahora, venceEn, ESTADO_PENDIENTE);
        return RespuestaServicio.creado(guardada);
    }

    public RespuestaServicio<AsignacionDto> validarCodigo(ValidarCodigoRequest solicitud) {
        if (solicitud == null || solicitud.codigo() == null) {
            return RespuestaServicio.error(HttpStatus.BAD_REQUEST, "Ingresa el código");
        }
        String codigo = solicitud.codigo().trim();
        if (codigo.length() != 6 || !codigo.chars().allMatch(Character::isDigit)) {
            return RespuestaServicio.error(HttpStatus.BAD_REQUEST, "El código debe tener 6 números");
        }

        Optional<AsignacionDto> asignacion = asignaciones.buscarPorCodigo(codigo);
        if (asignacion.isEmpty()) {
            return RespuestaServicio.error(HttpStatus.NOT_FOUND, "Ese código no existe");
        }

        AsignacionDto actual = asignacion.get();
        long ahora = reloj.ahora();
        if (ESTADO_FINALIZADA.equals(actual.estado())) {
            return RespuestaServicio.error(HttpStatus.CONFLICT, "Ese código ya se usó");
        }
        if (actual.venceEn() < ahora) {
            return RespuestaServicio.error(HttpStatus.GONE, "Ese código ya venció");
        }
        return RespuestaServicio.ok(actual);
    }
}