package edu.adoo.escrims.patterns.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DomainEventBus {
    private final List<Subscriber> subscribers = new ArrayList<>();

    public void subscribe(Subscriber subscriber) {
        subscribers.add(Objects.requireNonNull(subscriber));
    }

    public void unsubscribe(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }

    public void publish(DomainEvent event) {
        for (Subscriber subscriber : List.copyOf(subscribers)) {
            subscriber.onEvent(event);
        }
    }
}
