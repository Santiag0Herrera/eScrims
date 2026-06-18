package edu.adoo.escrims.patterns.factory;

import edu.adoo.escrims.patterns.adapter.ExternalNotificationProvider;
import edu.adoo.escrims.patterns.observer.DomainEvent;

import java.util.Map;
import java.util.Objects;

public class EmailNotifier implements Notifier {
    private final ExternalNotificationProvider provider;

    public EmailNotifier(ExternalNotificationProvider provider) {
        this.provider = Objects.requireNonNull(provider);
    }

    @Override
    public void send(DomainEvent event) {
        Map<String, String> payload = event.getPayload();
        String jugadores = payload.get("jugadores");

        if (jugadores == null || jugadores.isBlank()) {
            return;
        }

        String body = buildBody(payload);
        for (String email : jugadores.split(",")) {
            provider.sendMessage(email.trim(), body);
        }
    }

    private String buildBody(Map<String, String> payload) {
        StringBuilder body = new StringBuilder();
        body.append("Hola, te informamos que el scrim en el que participaste ha finalizado.\n\n");
        body.append("=== RESUMEN DEL SCRIM ===\n");
        body.append("Juego:      ").append(payload.getOrDefault("juego", "-")).append("\n");
        body.append("Formato:    ").append(payload.getOrDefault("formato", "-")).append("\n");
        body.append("Modalidad:  ").append(payload.getOrDefault("modalidad", "-")).append("\n");
        body.append("Region:     ").append(payload.getOrDefault("region", "-")).append("\n");
        body.append("Fecha:      ").append(payload.getOrDefault("fecha", "-")).append("\n");
        body.append("Estado:     ").append(payload.getOrDefault("estado", "-")).append("\n");

        String estadisticas = payload.get("estadisticas");
        if (estadisticas != null && !estadisticas.isBlank()) {
            body.append("\n=== ESTADISTICAS POR EQUIPO ===\n");
            body.append(estadisticas);
        }

        body.append("\nGracias por participar en eScrims!\n");
        return body.toString();
    }
}
