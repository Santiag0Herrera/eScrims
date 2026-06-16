package edu.adoo.escrims.patterns.adapter;

import java.util.function.Consumer;

public class DiscordAPI {
    private final String webhookUrl;
    private final Consumer<String> logger;

    public DiscordAPI(String webhookUrl, Consumer<String> logger) {
        this.webhookUrl = webhookUrl;
        this.logger = logger;
    }

    public void postToChannel(String channelId, String message) {
        logger.accept("[DiscordAPI] " + webhookUrl + " -> #" + channelId + ": " + message);
    }
}
