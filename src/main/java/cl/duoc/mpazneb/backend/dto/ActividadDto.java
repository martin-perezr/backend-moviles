package cl.duoc.mpazneb.backend.dto;

public record ActividadDto(
    Long id,
    String nombre,
    String modulo,
    Integer curso,
    String contenido,
    String objetivo,
    Integer dificultad,
    String instrucciones,
    Integer intentosPermitidos,
    String estado,
    String textoTitulo,
    String textoCuerpo,
    String imagenRuta
) {}