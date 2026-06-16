package edu.adoo.escrims.patterns.factory;

import edu.adoo.escrims.patterns.adapter.DiscordAdapter;
import edu.adoo.escrims.patterns.adapter.ExternalNotificationProvider;
import edu.adoo.escrims.patterns.adapter.FirebaseAdapter;
import edu.adoo.escrims.patterns.adapter.SendGridAdapter;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class ChannelNotifierFactory implements NotifierFactory {
    private final Map<String, Supplier<Notifier>> canalesSoportados;

    public ChannelNotifierFactory(SendGridAdapter sendGridAdapter, FirebaseAdapter firebaseAdapter, DiscordAdapter discordAdapter) {
        ExternalNotificationProvider emailProvider = Objects.requireNonNull(sendGridAdapter);
        ExternalNotificationProvider pushProvider = Objects.requireNonNull(firebaseAdapter);
        ExternalNotificationProvider discordProvider = Objects.requireNonNull(discordAdapter);
        this.canalesSoportados = Map.of(
                "EMAIL", () -> new EmailNotifier(emailProvider),
                "PUSH", () -> new PushNotifier(pushProvider),
                "DISCORD", () -> new DiscordNotifier(discordProvider)
        );
    }

    @Override
    public Notifier createNotifier(String canal) {
        Supplier<Notifier> supplier = canalesSoportados.get(canal.toUpperCase());
        if (supplier == null) {
            throw new IllegalArgumentException("Canal no soportado: " + canal);
        }
        return supplier.get();
    }
}
