package com.sopheak.alert_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class TelegramService {

    private final RestClient restClient;
    private final String botToken;
    private final String chatId;

    public TelegramService(
            @Value("${telegram.bot-token}") String botToken,
            @Value("${telegram.chat-id}") String chatId) {

        this.botToken = botToken;
        this.chatId = chatId;
        this.restClient = RestClient.create();
    }

    public void sendMessage(String message) {
        String url = "https://api.telegram.org/bot"
                + botToken + "/sendMessage";

        restClient.post()
                .uri(url)
                .body(new TelegramMessage(chatId, message))
                .retrieve()
                .toBodilessEntity();
    }

    private record TelegramMessage(String chat_id, String text) {
    }
}