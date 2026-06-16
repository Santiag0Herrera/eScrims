package edu.adoo.escrims.patterns.adapter;

import java.util.Objects;

public class FirebaseAdapter implements ExternalNotificationProvider {
    private final FirebaseAPI api;

    public FirebaseAdapter(FirebaseAPI api) {
        this.api = Objects.requireNonNull(api);
    }

    @Override
    public void sendMessage(String destination, String payload) {
        api.sendPush(destination, payload);
    }
}
