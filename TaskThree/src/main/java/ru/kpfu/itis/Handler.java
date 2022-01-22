package ru.kpfu.itis;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.kpfu.itis.models.Messages;

import javax.jms.JMSException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

class Request {
    public String httpMethod;
    public String body;
}

class Response {
    public int statusCode;
    public String body;

    public Response(int statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
    }
}

public class Handler implements Function<String, Response> {
    @Override
    public Response apply(String body) {
        ObjectMapper objectMapper = new ObjectMapper();
        Messages messages = null;
        System.out.println(body);
        try {
            messages = objectMapper.readValue(body, Messages.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> dataForBot = new ArrayList<>();
        String[] photoNames = messages.getMessages().get(0).getDetails().getMessage().getBody().split(" ");
        for (String photoName : photoNames) {
            AmazonS3 s3 = S3InitConnection.getInstance();
            S3Object s3Object = s3.getObject("vvot", photoName);
            String toList = s3Object.getObjectContent().getHttpRequest().getURI().toString() + " " + photoName;
            System.out.println(toList);
            dataForBot.add(toList);
        }
        BotExecution botExecution = new BotExecution();
        botExecution.initConnection();
        botExecution.sendMessage(dataForBot);
        return new Response(200, "Okaaaay lets go");
    }
}
