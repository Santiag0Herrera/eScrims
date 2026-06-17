package edu.adoo.escrims.patterns.adapter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Objects;
import java.util.function.Consumer;

public class DiscordAPI {
    private final String webhookUrl;
    private final Consumer<String> logger;
    private final HttpClient client;

    public DiscordAPI(String webhookUrl, Consumer<String> logger) {
        this.webhookUrl = webhookUrl;
        this.logger = Objects.requireNonNull(logger);
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public void postToChannel(String channelId, String message) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            logger.accept("[DiscordAPI] No se envio el mensaje: falta configurar DISCORD_WEBHOOK_URL");
            return;
        }

        String content = "**eScrims | #" + channelId + "**\n" + message;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(webhookUrl))
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(toDiscordPayload(content)))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    int status = response.statusCode();
                    if (status >= 200 && status < 300) {
                        logger.accept("[DiscordAPI] Mensaje enviado a Discord #" + channelId);
                    } else {
                        logger.accept("[DiscordAPI] Error Discord HTTP " + status + ": " + response.body());
                    }
                })
                .exceptionally(error -> {
                    logger.accept("[DiscordAPI] No se pudo contactar Discord: " + error.getMessage());
                    return null;
                });
    }

    private String toDiscordPayload(String content) {
        return "{\"content\":\"" + escapeJson(content) + "\",\"allowed_mentions\":{\"parse\":[]}}";
    }

    private String escapeJson(String value) {
        StringBuilder escaped = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);
            switch (character) {
                case '"' -> escaped.append("\\\"");
                case '\\' -> escaped.append("\\\\");
                case '\b' -> escaped.append("\\b");
                case '\f' -> escaped.append("\\f");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> {
                    if (character < 0x20) {
                        escaped.append(String.format("\\u%04x", (int) character));
                    } else {
                        escaped.append(character);
                    }
                }
            }
        }
        return escaped.toString();
    }
}
