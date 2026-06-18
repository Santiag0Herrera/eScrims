package edu.adoo.escrims.patterns.observer;

import edu.adoo.escrims.patterns.factory.Notifier;
import edu.adoo.escrims.patterns.factory.NotifierFactory;

import java.util.Map;
import java.util.Objects;

public class NotificationSubscriber implements Subscriber {
    private final Map<String, NotifierFactory> factoriesByRegion;

    public NotificationSubscriber(Map<String, NotifierFactory> factoriesByRegion) {
        this.factoriesByRegion = Map.copyOf(Objects.requireNonNull(factoriesByRegion));
    }

    @Override
    public void onEvent(DomainEvent event) {
        NotifierFactory factory = factoryFor(event.getRegion());
        Notifier primary = factory.createPrimaryNotifier();
        Notifier secondary = factory.createSecondaryNotifier();
        primary.send(event);
        secondary.send(event);
    }

    private NotifierFactory factoryFor(String region) {
        String normalizedRegion = normalizeRegion(region);
        NotifierFactory factory = factoriesByRegion.get(normalizedRegion);
        if (factory == null) {
            throw new IllegalArgumentException("No hay fabrica configurada para la region: " + region);
        }
        return factory;
    }

    private String normalizeRegion(String region) {
        String value = Objects.requireNonNull(region).trim().toUpperCase();
        return switch (value) {
            case "LAS", "LAN", "BR", "LATAM" -> "LATAM";
            case "EU", "EUROPA", "EUROPE" -> "EU";
            case "NA", "NORTEAMERICA", "NORTEAMÉRICA", "NORTHAMERICA", "USA", "US" -> "NA";
            default -> value;
        };
    }
}
