package ru.itis.rapashin.task.second.utils;

import java.io.IOException;
import java.io.Reader;

/**
 * @author <a href="mailto:ruslan.pashin@waveaccess.ru">Ruslan Pashin</a>
 */
public class SimpleUtil {

    private SimpleUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
