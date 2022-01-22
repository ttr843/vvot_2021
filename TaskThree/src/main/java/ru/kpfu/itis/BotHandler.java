package ru.kpfu.itis;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BotHandler extends TelegramLongPollingBot {
    @Override
    public String getBotUsername() {
        return "vvotRuslan_bot";
    }

    @Override
    public String getBotToken() {
        return "5275720776:AAHicb0j2-4wRP-HeeScDImJTrg4Sgsn5KY";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.isReply()) {
                Message reply = message.getReplyToMessage();
                if (message.hasText()) {
                    try {
                        tieFaceAndName(reply.getText(), message.getText());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    SendMessage sendMessageRequest = new SendMessage();
                    sendMessageRequest.setChatId(message.getChatId().toString());
                    sendMessageRequest.setText("Ответь на сообщение еще раз, я понимаю только текст:(");
                    try {
                        execute(sendMessageRequest);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (message.hasText()) {
                if (message.getText().startsWith("/find")) {
                    String name = message.getText().substring(6);
                    System.out.println(name);
                    List<String> photoURI = new ArrayList<>();
                    try {
                        if (getPhotoByName(name) == null || getPhotoByName(name).isEmpty()) {
                            SendMessage sendMessageRequest = new SendMessage();
                            sendMessageRequest.setChatId(message.getChatId().toString());
                            sendMessageRequest.setText("Фото с " + name + " нет");
                            execute(sendMessageRequest);
                        } else {
                            try {
                                photoURI = getPhotoURI(getPhotoByName(name));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            for (String s : photoURI) {
                                SendMessage sendMessageRequest = new SendMessage();
                                sendMessageRequest.setChatId(message.getChatId().toString());
                                sendMessageRequest.setText(s);
                                try {
                                    execute(sendMessageRequest);
                                } catch (TelegramApiException e) {
                                    throw new IllegalArgumentException(e);
                                }
                            }
                        }
                    } catch (IOException | TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    public void tieFaceAndName(String messageText, String name) throws IOException {
        String photoName = messageText.split(" ")[1];
        String originalPhotoName = "";
        //Ищу оригинальную фотографию
        AmazonS3 s3 = S3InitConnection.getInstance();
        S3Object faceObject = s3.getObject("vvot", "faces.json");
        S3ObjectInputStream s3ObjectInputStream = faceObject.getObjectContent();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(s3ObjectInputStream));
        String jsonText = readAll(bufferedReader);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, List<String>> jsonMap = objectMapper.readValue(jsonText, new TypeReference<Map<String, List<String>>>() {
        });
        System.out.println("photoName: " + photoName);
        for (String fileName : jsonMap.keySet()) {
            System.out.println("Current fileName " + fileName);
            List<String> faces = jsonMap.get(fileName);
            for (String face : faces) {
                System.out.println("Current face + " + face);
                if (face.equals(photoName)) {
                    originalPhotoName = fileName;
                    System.out.println(originalPhotoName);
                    break;
                }
            }
        }
        //Сохраняю имя человека в JSON файл
        List<String> names = new ArrayList<>();
        names.add(name);
        S3Object nammingObject = s3.getObject("vvot", "naming.json");
        S3ObjectInputStream s3InputStream = nammingObject.getObjectContent();
        BufferedReader br = new BufferedReader(new InputStreamReader(s3InputStream));
        jsonText = readAll(br);
        if (jsonText.isEmpty()) {
            JSONObject json = new JSONObject();
            json.put(originalPhotoName, names);
            s3.putObject("vvot", "naming.json", json.toString());
        } else {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, List<String>> jsonMapNaming = mapper.readValue(jsonText, new TypeReference<Map<String, List<String>>>() {
            });
            if (!jsonMapNaming.containsKey(originalPhotoName)) {
                jsonMapNaming.put(originalPhotoName, names);
                s3.putObject("vvot", "naming.json", new ObjectMapper().writeValueAsString(jsonMapNaming));
            } else {
                for (String face : names) {
                    jsonMapNaming.get(originalPhotoName).add(face);
                }
                s3.putObject("vvot", "naming.json", new ObjectMapper().writeValueAsString(jsonMapNaming));
            }
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public List<String> getPhotoByName(String name) throws IOException {
        List<String> photoList = new ArrayList<>();
        AmazonS3 s3 = S3InitConnection.getInstance();
        S3Object s3Object = s3.getObject("vvot", "naming.json");
        S3ObjectInputStream s3InputStream = s3Object.getObjectContent();
        BufferedReader br = new BufferedReader(new InputStreamReader(s3InputStream));
        String jsonText = readAll(br);
        if (jsonText.isEmpty()) {
            return null;
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, List<String>> jsonMap = objectMapper.readValue(jsonText, new TypeReference<Map<String, List<String>>>() {
            });
            for (String fileName : jsonMap.keySet()) {
                List<String> names = jsonMap.get(fileName);
                for (String nameInJson : names) {
                    if (name.equals(nameInJson)) {
                        photoList.add(fileName);
                    }
                }
            }
        }
        return photoList;
    }

    public List<String> getPhotoURI(List<String> photoName) {
        List<String> photoURI = new ArrayList<>();
        for (String s : photoName) {
            AmazonS3 s3 = S3InitConnection.getInstance();
            S3Object s3Object = s3.getObject("vvot", s);
            photoURI.add(s3Object.getObjectContent().getHttpRequest().getURI().toString());
        }
        return photoURI;
    }
}
