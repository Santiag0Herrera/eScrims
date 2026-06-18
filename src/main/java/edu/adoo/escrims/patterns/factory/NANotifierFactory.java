package edu.adoo.escrims.patterns.factory;

import edu.adoo.escrims.patterns.adapter.DiscordAdapter;
import edu.adoo.escrims.patterns.adapter.ExternalNotificationProvider;
import edu.adoo.escrims.patterns.adapter.FirebaseAdapter;

import java.util.Objects;

public class NANotifierFactory implements NotifierFactory {
    private final ExternalNotificationProvider pushProvider;
    private final ExternalNotificationProvider discordProvider;

    public NANotifierFactory(FirebaseAdapter firebaseAdapter, DiscordAdapter discordAdapter) {
        this.pushProvider = Objects.requireNonNull(firebaseAdapter);
        this.discordProvider = Objects.requireNonNull(discordAdapter);
    }

    @Override
    public Notifier createPrimaryNotifier() {
        return new PushNotifier(pushProvider);
    }

    @Override
    public Notifier createSecondaryNotifier() {
        return new DiscordNotifier(discordProvider);
    }
}
