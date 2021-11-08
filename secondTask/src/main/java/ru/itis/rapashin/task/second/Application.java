package ru.itis.rapashin.task.second;

import org.json.JSONException;
import ru.itis.rapashin.task.second.service.FaceService;

import javax.jms.JMSException;
import java.io.IOException;

/**
 * @author <a href="mailto:ruslan.pashin@waveaccess.ru">Ruslan Pashin</a>
 */
public class Application {

    public static void main(String[] args) throws IOException, JSONException, JMSException {
        FaceService faceService = new FaceService();
        faceService.getFaces(args[1]);
    }
}
