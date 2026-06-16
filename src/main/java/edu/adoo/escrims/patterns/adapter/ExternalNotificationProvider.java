package edu.adoo.escrims.patterns.adapter;

public interface ExternalNotificationProvider {
    void sendMessage(String destination, String payload);
}
