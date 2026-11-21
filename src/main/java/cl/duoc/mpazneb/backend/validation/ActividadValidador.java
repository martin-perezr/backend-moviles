package cl.duoc.mpazneb.backend.validation;

import cl.duoc.mpazneb.backend.dto.ActividadRequest;
import java.util.Set;

public final class ActividadValidador {
    private static final Set<String> MODULOS = Set.of("LECTURA", "MATEMATICA");
    private static final Set<String> ESTADOS = Set.of("BORRADOR", "DISPONIBLE", "DESACTIVADA");

    private ActividadValidador() {}

    /** Devuelve el mensaje de error, o null si la solicitud es válida. */
    public static String validar(ActividadRequest r) {
        if (r == null) return "Faltan los datos de la actividad";

        String nombre = r.nombre() == null ? "" : r.nombre().trim();
        if (nombre.isEmpty()) return "Escribe el nombre de la actividad";
        if (nombre.length() < 4) return "El nombre debe tener al menos 4 caracteres";
        if (nombre.length() > 60) return "El nombre no puede superar 60 caracteres";

        if (r.modulo() == null || !MODULOS.contains(r.modulo()))
            return "Elige Lectura o Matemática";
        if (r.curso() == null || r.curso() < 1 || r.curso() > 4)
            return "Elige un curso de 1.º a 4.º básico";
        if (r.dificultad() == null || r.dificultad() < 1 || r.dificultad() > 3)
            return "Elige una dificultad";

        String contenido = r.contenido() == null ? "" : r.contenido().trim();
        if (contenido.length() < 4) return "El contenido es demasiado corto";

        String objetivo = r.objetivo() == null ? "" : r.objetivo().trim();
        if (objetivo.length() < 8) return "El objetivo debe explicar qué se practica";

        String instrucciones = r.instrucciones() == null ? "" : r.instrucciones().trim();
        if (instrucciones.length() < 8) return "Las instrucciones deben ser más claras";

        if (r.intentosPermitidos() == null
                || r.intentosPermitidos() < 1 || r.intentosPermitidos() > 5)
            return "Los intentos van de 1 a 5";

        if (r.estado() == null || !ESTADOS.contains(r.estado()))
            return "Estado inválido";

        if ("LECTURA".equals(r.modulo())) {
            String texto = r.textoCuerpo() == null ? "" : r.textoCuerpo().trim();
            if (texto.length() < 20) return "El texto debe tener al menos 20 caracteres";
        }
        return null;
    }
}