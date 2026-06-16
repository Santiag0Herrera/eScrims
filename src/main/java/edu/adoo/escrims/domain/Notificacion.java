package edu.adoo.escrims.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Notificacion {
    private final String id;
    private final String tipo;
    private final String canal;
    private final String payload;
    private String estado;
    private LocalDateTime fechaEnvio;

    public Notificacion(String id, String tipo, String canal, String payload) {
        this.id = Objects.requireNonNull(id);
        this.tipo = Objects.requireNonNull(tipo);
        this.canal = Objects.requireNonNull(canal);
        this.payload = Objects.requireNonNull(payload);
        this.estado = "PENDIENTE";
    }

    public void enviar() {
        estado = "ENVIADA";
        fechaEnvio = LocalDateTime.now();
    }

    public void reintentar() {
        estado = "REINTENTADA";
        enviar();
    }

    public String getCanal() {
        return canal;
    }

    public String getPayload() {
        return payload;
    }
}
