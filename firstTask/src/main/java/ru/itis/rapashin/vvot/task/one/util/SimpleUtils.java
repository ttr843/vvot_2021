package ru.itis.rapashin.vvot.task.one.util;

/**
 * @author <a href="mailto:ruslan.pashin@waveaccess.ru">Ruslan Pashin</a>
 */
public class SimpleUtils {
    private static final String SLASH = "\\";

    public String pathValidator(String path) {
        if (path.endsWith(SLASH)) return path;
        else return path + SLASH;
    }
}
