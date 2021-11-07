package ru.itis.rapashin.vvot.task.one.config;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

/**
 * @author <a href="mailto:ruslan.pashin@waveaccess.ru">Ruslan Pashin</a>
 */
public class Configuration {

    private static AmazonS3 amazonS3;

    private Configuration() {
        throw new IllegalStateException("Utility class");
    }

    private static AmazonS3 initAmazonS3() {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new ProfileCredentialsProvider())
                .withEndpointConfiguration(
                        new AmazonS3ClientBuilder.EndpointConfiguration(
                                "storage.yandexcloud.net", "ru-central1"
                        )
                )
                .build();
    }

    public static AmazonS3 getInstance() {
        if (amazonS3 == null) {
            amazonS3 = initAmazonS3();
        }
        return amazonS3;
    }
}
