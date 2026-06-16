package edu.adoo.escrims.patterns.adapter;

import java.util.function.Consumer;

public class SendGridAPI {
    private final String apiKey;
    private final Consumer<String> logger;

    public SendGridAPI(String apiKey, Consumer<String> logger) {
        this.apiKey = apiKey;
        this.logger = logger;
    }

    public void sendEmail(String email, String subject, String body) {
        logger.accept("[SendGridAPI] " + subject + " -> " + email + ": " + body);
    }
}
