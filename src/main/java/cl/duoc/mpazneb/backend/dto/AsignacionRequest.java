package cl.duoc.mpazneb.backend.dto;

public record AsignacionRequest(
    Long estudianteId,
    Long actividadId,
    Long asistenteId
) {}