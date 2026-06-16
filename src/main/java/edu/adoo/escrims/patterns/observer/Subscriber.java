package edu.adoo.escrims.patterns.observer;

public interface Subscriber {
    void onEvent(DomainEvent event);
}
