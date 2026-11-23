package cl.duoc.mpazneb.backend.service;

import org.springframework.stereotype.Component;

@Component
public class RelojSistema implements Reloj {

    @Override
    public long ahora() {
        return System.currentTimeMillis();
    }
}