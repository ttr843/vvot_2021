package ru.itis.rapashin.task.second.configuration;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

/**
 * @author <a href="mailto:ruslan.pashin@waveaccess.ru">Ruslan Pashin</a>
 */
public class Configuration {

    private static final String SERVICE_ENDPOINT = "storage.yandexcloud.net";
    private static final String SIGNING_REGION = "ru-central1";
    private static AmazonS3 amazonS3;

    private Configuration() {
        throw new IllegalStateException("Configuration class");
    }

    private static AmazonS3 initAmazonS3() {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new ProfileCredentialsProvider())
                .withEndpointConfiguration(
                        new AmazonS3ClientBuilder.EndpointConfiguration(
                                SERVICE_ENDPOINT, SIGNING_REGION
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
