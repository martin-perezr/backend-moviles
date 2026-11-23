package cl.duoc.mpazneb.backend.service;

import java.security.SecureRandom;
import java.util.function.Predicate;
import org.springframework.stereotype.Component;

@Component
public class GeneradorCodigoTemporal {
    private static final int INTENTOS_MAX = 50;
    private final SecureRandom random = new SecureRandom();

    /**
     * Genera un código numérico de seis caracteres, conservando ceros a la izquierda.
     * El predicado indica si el código ya está en uso entre asignaciones vigentes.
     */
    public String generarUnico(Predicate<String> codigoEnUso) {
        for (int i = 0; i < INTENTOS_MAX; i++) {
            String codigo = String.format("%06d", random.nextInt(1_000_000));
            if (!codigoEnUso.test(codigo)) {
                return codigo;
            }
        }
        throw new IllegalStateException("No fue posible generar un código único");
    }
}