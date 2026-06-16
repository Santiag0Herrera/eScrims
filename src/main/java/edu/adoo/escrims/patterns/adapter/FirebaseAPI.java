package edu.adoo.escrims.patterns.adapter;

import java.util.function.Consumer;

public class FirebaseAPI {
    private final String projectId;
    private final Consumer<String> logger;

    public FirebaseAPI(String projectId, Consumer<String> logger) {
        this.projectId = projectId;
        this.logger = logger;
    }

    public void sendPush(String token, String message) {
        logger.accept("[FirebaseAPI] " + projectId + " -> " + token + ": " + message);
    }
}
