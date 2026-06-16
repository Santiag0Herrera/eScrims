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
        provider.sendMessage("mobile-token-demo", event.describir());
    }
}
