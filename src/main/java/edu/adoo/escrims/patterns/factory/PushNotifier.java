package edu.adoo.escrims.patterns.factory;

import edu.adoo.escrims.patterns.adapter.ExternalNotificationProvider;
import edu.adoo.escrims.patterns.observer.DomainEvent;

import java.util.Objects;

public class PushNotifier implements Notifier {
    private final ExternalNotificationProvider provider;

    public PushNotifier(ExternalNotificationProvider provider) {
        this.provider = Objects.requireNonNull(provider);
    }

    @Override
    public void send(DomainEvent event) {
        provider.sendMessage("mobile-token-demo", mensajePara(event));
    }

    private String mensajePara(DomainEvent event) {
        String estado = event.getPayload().get("estado");
        String scrim = event.getPayload().get("scrim");
        if ("EnJuego".equals(estado)) {
            return "Tu scrim ya empezo. Entra al lobby y coordina con tu equipo.";
        }
        if ("Confirmado".equals(estado)) {
            return "Tu scrim esta confirmado. Revisa los detalles antes de jugar.";
        }
        if (estado != null && scrim != null) {
            return "El scrim " + scrim + " cambio a estado " + estado + ".";
        }
        return "Tenes una nueva notificacion de eScrims.";
    }
}
