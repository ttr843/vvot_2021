package ru.itis.rapashin.vvot.task.one.model;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import ru.itis.rapashin.vvot.task.one.config.Configuration;
import ru.itis.rapashin.vvot.task.one.util.SimpleUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:ruslan.pashin@waveaccess.ru">Ruslan Pashin</a>
 */
public class Photo {
    private static final String ALBUMS_JSON = "albums.json";
    private static final String VVOT = "vvot";
    private static final String JPG_EXTENSION = "jpg";
    private static final String JPEG_EXTENSION = "jpeg";
    private static final Logger logger = Logger.getLogger(Photo.class.getName());

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static boolean isPathValid(String path) {
        try {
            return Files.isDirectory(Paths.get(path));
        } catch (InvalidPathException ex) {
            return false;
        }
    }

    public void upload(String path, String album) {
        try (Stream<Path> paths = Files.walk(Paths.get(path), 1)) {
            List<Path> files = paths
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
            AmazonS3 s3 = Configuration.getInstance();
            List<Path> serverFilesPaths = getOnlyImages(files);
            for (int i = 0; i < serverFilesPaths.size(); i++) {
                s3.putObject(VVOT, serverFilesPaths.get(i).toString(), new File(files.get(i).toString()));
            }
            putInAlbum(s3, album, serverFilesPaths);
        } catch (IOException e) {
            logger.log(Level.INFO, "path is not present");
        }
    }

    public List<Path> getOnlyImages(List<Path> files) {
        List<Path> validatedPaths = new ArrayList<>();
        for (Path file : files) {
            String filename = file.toString();
            String fileExtension = filename.substring(filename.lastIndexOf(".") + 1);
            if (fileExtension.equals(JPG_EXTENSION) || fileExtension.equals(JPEG_EXTENSION)) {
                validatedPaths.add(Paths.get(filename.substring(filename.lastIndexOf("\\") + 1)));
            }
        }
        return validatedPaths;
    }

    private void putInAlbum(AmazonS3 s3, String album, List<Path> serverPaths) {
        S3Object albumObject = s3.getObject(VVOT, ALBUMS_JSON);
        S3ObjectInputStream s3ObjectInputStream = albumObject.getObjectContent();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(s3ObjectInputStream));
        try {
            String jsonText = readAll(bufferedReader);
            if (jsonText.isEmpty()) {
                JSONObject json = new JSONObject();
                json.put(album, serverPaths);
                s3.putObject(VVOT, ALBUMS_JSON, json.toString());
            } else {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, List<String>> jsonMap = objectMapper.readValue(jsonText, new TypeReference<Map<String, List<String>>>() {
                });
                if (!jsonMap.containsKey(album)) {
                    ArrayList<String> pathList = new ArrayList<>();
                    for (Path serverPath : serverPaths) {
                        pathList.add(serverPath.toString());
                    }
                    jsonMap.put(album, pathList);
                    s3.putObject(VVOT, ALBUMS_JSON, new ObjectMapper().writeValueAsString(jsonMap));
                } else {
                    for (Path serverPath : serverPaths) {
                        jsonMap.get(album).add(serverPath.toString());
                    }
                    s3.putObject(VVOT, ALBUMS_JSON, new ObjectMapper().writeValueAsString(jsonMap));
                }
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void download(String path, String album) {
        if (isPathValid(path)) {
            SimpleUtils simpleUtils = new SimpleUtils();
            path = simpleUtils.pathValidator(path);
            AmazonS3 s3 = Configuration.getInstance();
            S3Object albumObject = s3.getObject(VVOT, ALBUMS_JSON);
            S3ObjectInputStream s3ObjectInputStream = albumObject.getObjectContent();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(s3ObjectInputStream));
            try {
                String jsonText = readAll(bufferedReader);
                if (jsonText.isEmpty()) {
                    logger.log(Level.INFO, "no one album is present");
                } else {
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map<String, List<String>> jsonMap = objectMapper.readValue(jsonText, new TypeReference<Map<String, List<String>>>() {
                    });
                    if (!jsonMap.containsKey(album)) {
                        logger.log(Level.INFO, "this album not present");
                    } else {
                        List<String> pathList = jsonMap.get(album);
                        for (String s : pathList) {
                            S3Object photo = s3.getObject(VVOT, s);
                            S3ObjectInputStream inputStream = photo.getObjectContent();
                            FileUtils.copyInputStreamToFile(inputStream, new File(path + photo.getKey()));
                        }
                    }
                }

            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        } else {
            logger.log(Level.INFO, "bad path");
        }
    }

    public Set<String> list() {
        AmazonS3 s3 = Configuration.getInstance();
        S3Object albumObject = s3.getObject(VVOT, ALBUMS_JSON);
        S3ObjectInputStream s3ObjectInputStream = albumObject.getObjectContent();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(s3ObjectInputStream));
        try {
            String jsonText = readAll(bufferedReader);
            if (jsonText.isEmpty()) {
                return Collections.emptySet();
            } else {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, List<String>> jsonMap = objectMapper.readValue(jsonText, new TypeReference<Map<String, List<String>>>() {
                });
                return jsonMap.keySet();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptySet();
    }

    public List<String> list(String album) {
        AmazonS3 s3 = Configuration.getInstance();
        S3Object albumObject = s3.getObject(VVOT, ALBUMS_JSON);
        S3ObjectInputStream s3ObjectInputStream = albumObject.getObjectContent();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(s3ObjectInputStream));
        try {
            String jsonText = readAll(bufferedReader);
            if (jsonText.isEmpty()) {
                return Collections.emptyList();
            } else {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, List<String>> jsonMap = objectMapper.readValue(jsonText, new TypeReference<Map<String, List<String>>>() {
                });
                return jsonMap.get(album);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
