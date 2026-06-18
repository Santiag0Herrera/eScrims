package edu.adoo.escrims.patterns.factory;

public interface NotifierFactory {
    Notifier createPrimaryNotifier();
    Notifier createSecondaryNotifier();
}
