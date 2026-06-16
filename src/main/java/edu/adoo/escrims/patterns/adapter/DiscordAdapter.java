package edu.adoo.escrims.patterns.adapter;

import java.util.Objects;

public class DiscordAdapter implements ExternalNotificationProvider {
    private final DiscordAPI api;

    public DiscordAdapter(DiscordAPI api) {
        this.api = Objects.requireNonNull(api);
    }

    @Override
    public void sendMessage(String destination, String payload) {
        api.postToChannel(destination, payload);
    }
}
