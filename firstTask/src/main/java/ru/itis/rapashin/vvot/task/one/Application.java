package ru.itis.rapashin.vvot.task.one;

import ru.itis.rapashin.vvot.task.one.model.Photo;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:ruslan.pashin@waveaccess.ru">Ruslan Pashin</a>
 */
public class Application {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger(Application.class.getName());
        Photo photo = new Photo();
        switch (args[1].toLowerCase()) {
            case "upload":
                photo.upload(args[3], args[5]);
                break;
            case "download":
                photo.download(args[3], args[5]);
                break;
            case "list":
                try {
                    String s = args[3];
                    List<String> photos = photo.list(s);
                    if (photos == null) {
                        logger.log(Level.INFO, "album not present");
                        break;
                    } else {
                        if (photos.isEmpty()) logger.log(Level.INFO, "no photos in album");
                        else photos.forEach(ph ->
                                logger.log(Level.INFO, ph));
                    }
                    break;
                } catch (ArrayIndexOutOfBoundsException e) {
                    Set<String> albums = photo.list();
                    if (albums == null) {
                        logger.log(Level.INFO, "Size of albums is null");
                        break;
                    } else {
                        albums.forEach(al ->
                                logger.log(Level.INFO, al));
                    }
                    break;
                }
            default:
                break;
        }
    }
}
