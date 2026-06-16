package edu.adoo.escrims.patterns.observer;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class DomainEvent {
    private final String id;
    private final LocalDateTime timestamp;
    private final String tipo;
    private final String canal;
    private final Map<String, String> payload;

    public DomainEvent(String id, String tipo, String canal, Map<String, String> payload) {
        this.id = Objects.requireNonNull(id);
        this.tipo = Objects.requireNonNull(tipo);
        this.canal = Objects.requireNonNull(canal);
        this.payload = new LinkedHashMap<>(payload);
        this.timestamp = LocalDateTime.now();
    }

    public String describir() {
        return tipo + " por " + canal + " " + payload;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getTipo() {
        return tipo;
    }

    public String getCanal() {
        return canal;
    }

    public Map<String, String> getPayload() {
        return Collections.unmodifiableMap(payload);
    }
}
