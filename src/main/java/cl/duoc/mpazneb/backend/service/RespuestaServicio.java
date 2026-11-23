package cl.duoc.mpazneb.backend.service;

import org.springframework.http.HttpStatus;

public record RespuestaServicio<T>(HttpStatus estado, T cuerpo, String error) {

    public static <T> RespuestaServicio<T> ok(T cuerpo) {
        return new RespuestaServicio<>(HttpStatus.OK, cuerpo, null);
    }

    public static <T> RespuestaServicio<T> creado(T cuerpo) {
        return new RespuestaServicio<>(HttpStatus.CREATED, cuerpo, null);
    }

    public static <T> RespuestaServicio<T> error(HttpStatus estado, String mensaje) {
        return new RespuestaServicio<>(estado, null, mensaje);
    }

    public boolean esError() {
        return error != null;
    }
}