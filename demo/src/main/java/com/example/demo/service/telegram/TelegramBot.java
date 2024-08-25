
package com.example.demo.service.telegram;

import com.example.demo.config.TelegramBotConfig;
import com.example.demo.service.notification.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {
    private final TelegramBotConfig config;
    private final NotificationService notificationService;

    @Autowired
    public TelegramBot(TelegramBotConfig config, @Lazy NotificationService notificationService) {
        super(config.getToken());
        this.config = config;
        this.notificationService = notificationService;
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/subscribe":
                    subscribeCommandReceived(chatId);
                    break;
                case "/unsubscribe":
                    unsubscribeCommandReceived(chatId);
                    break;
                default: sendMessage(chatId, "Sorry, command was non recognized");
            }
        }
    }

    private void startCommandReceived(long chatId, String name) {
        String answer = "Hi, " + name + ", nice to meet you!";
        log.info("Replied to user: " + name);
        sendMessage(chatId, answer);
    }

    private void subscribeCommandReceived(long chatId) {
        boolean isSubscribed = notificationService.subscribe(chatId);
        String responseMessage = isSubscribed
                ? "You have been successfully subscribed to notifications."
                : "You are already subscribed.";
        sendMessage(chatId, responseMessage);
    }

    private void unsubscribeCommandReceived(long chatId) {
        boolean isUnsubscribed = notificationService.unsubscribe(chatId);
        String responseMessage = isUnsubscribed
                ? "You have been successfully unsubscribed from notifications."
                : "You are not subscribed.";
        sendMessage(chatId, responseMessage);
    }

    public void sendMessage(long chatId, String sendToText) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(sendToText);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }
}
