package edu.adoo.escrims.patterns.factory;

import edu.adoo.escrims.patterns.observer.DomainEvent;

public interface Notifier {
    void send(DomainEvent event);
}
