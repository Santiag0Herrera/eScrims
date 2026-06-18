package edu.adoo.escrims.patterns.factory;

import edu.adoo.escrims.patterns.adapter.ExternalNotificationProvider;
import edu.adoo.escrims.patterns.observer.DomainEvent;

import java.util.Map;
import java.util.Objects;

public class DiscordNotifier implements Notifier {
    private final ExternalNotificationProvider provider;

    public DiscordNotifier(ExternalNotificationProvider provider) {
        this.provider = Objects.requireNonNull(provider);
    }

    @Override
    public void send(DomainEvent event) {
        provider.sendMessage("scrims", humanize(event));
    }

    private String humanize(DomainEvent event) {
        Map<String, String> payload = event.getPayload();
        return switch (event.getTipo()) {
            case "PRUEBA_DISCORD" ->
                    "Conexion con Discord verificada correctamente para la region " + event.getRegion()
                            + ". Mensaje de prueba: " + payload.getOrDefault("mensaje", "Sin detalle.");
            case "SCRIM_BUSCANDOJUGADORES" ->
                    "El scrim " + scrimLabel(payload) + " quedo en estado Buscando jugadores para la region "
                            + event.getRegion() + ".";
            case "SCRIM_LOBBYARMADO" ->
                    "El scrim " + scrimLabel(payload) + " ya completo sus cupos y paso a Lobby armado en "
                            + event.getRegion() + ".";
            case "SCRIM_CONFIRMADO" ->
                    "El scrim " + scrimLabel(payload) + " fue confirmado en " + event.getRegion()
                            + " y ya puede prepararse para iniciar.";
            case "SCRIM_ENJUEGO" ->
                    "El scrim " + scrimLabel(payload) + " ya comenzo en " + event.getRegion()
                            + ". Que salga una buena partida.";
            case "SCRIM_FINALIZADO" ->
                    "El scrim " + scrimLabel(payload) + " finalizo en " + event.getRegion()
                            + ". Ya se pueden cargar estadisticas y feedback.";
            case "SCRIM_CANCELADO" ->
                    "El scrim " + scrimLabel(payload) + " fue cancelado en " + event.getRegion() + ".";
            default ->
                    "Nueva actualizacion de eScrims para " + event.getRegion() + ": "
                            + payload.getOrDefault("estado", event.getTipo()) + ".";
        };
    }

    private String scrimLabel(Map<String, String> payload) {
        return payload.getOrDefault("scrim", "sin identificador");
    }
}
