package edu.adoo.escrims.patterns.factory;

import edu.adoo.escrims.patterns.adapter.DiscordAdapter;
import edu.adoo.escrims.patterns.adapter.ExternalNotificationProvider;
import edu.adoo.escrims.patterns.adapter.SendGridAdapter;

import java.util.Objects;

public class LATAMNotifierFactory implements NotifierFactory {
    private final ExternalNotificationProvider emailProvider;
    private final ExternalNotificationProvider discordProvider;

    public LATAMNotifierFactory(SendGridAdapter sendGridAdapter, DiscordAdapter discordAdapter) {
        this.emailProvider = Objects.requireNonNull(sendGridAdapter);
        this.discordProvider = Objects.requireNonNull(discordAdapter);
    }

    @Override
    public Notifier createPrimaryNotifier() {
        return new EmailNotifier(emailProvider);
    }

    @Override
    public Notifier createSecondaryNotifier() {
        return new DiscordNotifier(discordProvider);
    }
}
