package ru.kpfu.itis;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;

public class BotExecution {
    final String chatID = "487127502";
    final BotHandler botHandler = new BotHandler();

    public void initConnection() {
        TelegramBotsApi telegramBotsApi = null;
        try {
            telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        try {
            telegramBotsApi.registerBot(botHandler);
        } catch (TelegramApiException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void sendMessage(List<String> photoUrls) {
        for (String photoUrl : photoUrls) {
            SendMessage photoMessageRequest = new SendMessage();
            photoMessageRequest.setChatId(chatID);
            photoMessageRequest.setText(photoUrl);
            try {
                botHandler.execute(photoMessageRequest);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            SendMessage sendMessageRequest = new SendMessage();
            sendMessageRequest.setChatId(chatID);
            sendMessageRequest.setText("Это кто????");
            try {
                botHandler.execute(sendMessageRequest);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
