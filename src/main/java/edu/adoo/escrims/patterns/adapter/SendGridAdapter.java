package edu.adoo.escrims.patterns.adapter;

import java.util.Objects;

public class SendGridAdapter implements ExternalNotificationProvider {
    private final SendGridAPI api;

    public SendGridAdapter(SendGridAPI api) {
        this.api = Objects.requireNonNull(api);
    }

    @Override
    public void sendMessage(String destination, String payload) {
        api.sendEmail(destination, "eScrims - Resumen del scrim", payload);
    }
}
