package ru.kpfu.itis;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.jms.JMSException;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws JMSException, InterruptedException, TelegramApiException {

        BotExecution botExecution = new BotExecution();
        botExecution.initConnection();
        botExecution.sendMessage(Arrays.asList("https://storage.yandexcloud.net/vvot/%5BFACE%5Dface-detection-sample0.jpeg [FACE]face-detection-sample0.jpeg", "https://storage.yandexcloud.net/vvot/%5BFACE%5Dface-detection-sample1.jpeg [FACE]face-detection-sample1.jpeg", "https://storage.yandexcloud.net/vvot/%5BFACE%5Dface-detection-sample2.jpeg [FACE]face-detection-sample2.jpeg"));
    }
}
