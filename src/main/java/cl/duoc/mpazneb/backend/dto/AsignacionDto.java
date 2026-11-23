package cl.duoc.mpazneb.backend.dto;

public record AsignacionDto(
    Long id,
    Long estudianteId,
    Long actividadId,
    Long asistenteId,
    String codigo,
    Long creadaEn,
    Long venceEn,
    String estado
) {}