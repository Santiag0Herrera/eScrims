package edu.adoo.escrims.patterns.observer;

import edu.adoo.escrims.patterns.factory.Notifier;
import edu.adoo.escrims.patterns.factory.NotifierFactory;

import java.util.Objects;

public class NotificationSubscriber implements Subscriber {
    private final NotifierFactory factory;

    public NotificationSubscriber(NotifierFactory factory) {
        this.factory = Objects.requireNonNull(factory);
    }

    @Override
    public void onEvent(DomainEvent event) {
        Notifier notifier = factory.createNotifier(event.getCanal());
        notifier.send(event);
    }
}
