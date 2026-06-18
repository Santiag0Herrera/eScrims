package edu.adoo.escrims.patterns.factory;

import edu.adoo.escrims.patterns.adapter.ExternalNotificationProvider;
import edu.adoo.escrims.patterns.adapter.FirebaseAdapter;
import edu.adoo.escrims.patterns.adapter.SendGridAdapter;

import java.util.Objects;

public class EUNotifierFactory implements NotifierFactory {
    private final ExternalNotificationProvider emailProvider;
    private final ExternalNotificationProvider pushProvider;

    public EUNotifierFactory(SendGridAdapter sendGridAdapter, FirebaseAdapter firebaseAdapter) {
        this.emailProvider = Objects.requireNonNull(sendGridAdapter);
        this.pushProvider = Objects.requireNonNull(firebaseAdapter);
    }

    @Override
    public Notifier createPrimaryNotifier() {
        return new EmailNotifier(emailProvider);
    }

    @Override
    public Notifier createSecondaryNotifier() {
        return new PushNotifier(pushProvider);
    }
}
