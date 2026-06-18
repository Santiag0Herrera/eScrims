package edu.adoo.escrims.patterns.adapter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

public class SendGridAPI {
    private static final String SENDGRID_URL = "https://api.sendgrid.com/v3/mail/send";
    private static final String FROM_EMAIL = "ignacioalmanza1509@gmail.com";
    private static final String FROM_NAME = "eScrims";

    private final String apiKey;
    private final Consumer<String> logger;
    private final HttpClient httpClient;

    public SendGridAPI(String apiKey, Consumer<String> logger) {
        this.apiKey = apiKey;
        this.logger = logger;
        this.httpClient = HttpClient.newHttpClient();
    }

    public void sendEmail(String toEmail, String subject, String body) {
        String json = buildJson(toEmail, subject, body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SENDGRID_URL))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 202) {
                        logger.accept("[SendGridAPI] Email enviado a " + toEmail + " | Subject: " + subject);
                    } else {
                        logger.accept("[SendGridAPI] Error " + response.statusCode() + ": " + response.body());
                    }
                })
                .exceptionally(ex -> {
                    logger.accept("[SendGridAPI] Error de red: " + ex.getMessage());
                    return null;
                });
    }

    private String buildJson(String toEmail, String subject, String body) {
        String safeSubject = subject.replace("\\", "\\\\").replace("\"", "\\\"");
        String safeBody = body.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
        return "{"
                + "\"personalizations\":[{\"to\":[{\"email\":\"" + toEmail + "\"}]}],"
                + "\"from\":{\"email\":\"" + FROM_EMAIL + "\",\"name\":\"" + FROM_NAME + "\"},"
                + "\"subject\":\"" + safeSubject + "\","
                + "\"content\":[{\"type\":\"text/plain\",\"value\":\"" + safeBody + "\"}]"
                + "}";
    }
}
