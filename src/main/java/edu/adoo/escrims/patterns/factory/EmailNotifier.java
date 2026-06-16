package edu.adoo.escrims.patterns.factory;

import edu.adoo.escrims.patterns.adapter.ExternalNotificationProvider;
import edu.adoo.escrims.patterns.observer.DomainEvent;

import java.util.Objects;

public class EmailNotifier implements Notifier {
    private final ExternalNotificationProvider provider;

    public EmailNotifier(ExternalNotificationProvider provider) {
        this.provider = Objects.requireNonNull(provider);
    }

    @Override
    public void send(DomainEvent event) {
        provider.sendMessage("organizador@escrims.local", event.describir());
    }
}
