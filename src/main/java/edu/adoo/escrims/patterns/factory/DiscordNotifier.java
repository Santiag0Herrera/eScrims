package edu.adoo.escrims.patterns.factory;

import edu.adoo.escrims.patterns.adapter.ExternalNotificationProvider;
import edu.adoo.escrims.patterns.observer.DomainEvent;

import java.util.Objects;

public class DiscordNotifier implements Notifier {
    private final ExternalNotificationProvider provider;

    public DiscordNotifier(ExternalNotificationProvider provider) {
        this.provider = Objects.requireNonNull(provider);
    }

    @Override
    public void send(DomainEvent event) {
        provider.sendMessage("scrims", event.describir());
    }
}
