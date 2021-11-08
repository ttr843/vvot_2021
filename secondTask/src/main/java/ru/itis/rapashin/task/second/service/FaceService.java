package ru.itis.rapashin.task.second.service;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import org.json.JSONException;
import org.json.JSONObject;
import ru.itis.rapashin.task.second.configuration.Configuration;
import ru.itis.rapashin.task.second.utils.SimpleUtil;
import yandex.cloud.api.ai.vision.v1.FaceDetection;
import yandex.cloud.api.ai.vision.v1.Primitives;
import yandex.cloud.api.ai.vision.v1.VisionServiceGrpc;
import yandex.cloud.api.ai.vision.v1.VisionServiceOuterClass;
import yandex.cloud.sdk.ServiceFactory;
import yandex.cloud.sdk.auth.Auth;
import yandex.cloud.sdk.auth.provider.CredentialProvider;

import javax.imageio.ImageIO;
import javax.jms.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:ruslan.pashin@waveaccess.ru">Ruslan Pashin</a>
 */
public class FaceService {

    private static final String VVOT = "vvot";
    private static final String JPEG_EXTENSION = "jpeg";
    private static final String FACES_JSON = "faces.json";
    private static final String QUEUE_NAME = "faceQueue";
    private static final String SERVICE_ENDPOINT = "https://message-queue.api.cloud.yandex.net";
    private static final String SIGNING_REGION = "ru-central1";
    private static final Logger logger = Logger.getLogger(FaceService.class.getName());

    public void getFaces(String filename) throws IOException, JSONException, JMSException {
        AmazonS3 s3 = Configuration.getInstance();
        S3Object photo = s3.getObject(VVOT, filename);
        S3ObjectInputStream inputStream = photo.getObjectContent();
        byte[] photoData = IOUtils.toByteArray(inputStream);
        CredentialProvider defaultComputeEngine = Auth.computeEngineBuilder().build();
        ServiceFactory factory = ServiceFactory.builder()
                .credentialProvider(defaultComputeEngine
                )
                .build();
        VisionServiceGrpc.VisionServiceBlockingStub visionService = factory.create(VisionServiceGrpc.VisionServiceBlockingStub.class, VisionServiceGrpc::newBlockingStub);
        VisionServiceOuterClass.BatchAnalyzeRequest request = VisionServiceOuterClass.BatchAnalyzeRequest.newBuilder()
                .setFolderId(UUID.randomUUID().toString())
                .addAnalyzeSpecs(VisionServiceOuterClass.AnalyzeSpec.newBuilder()
                        .addFeatures(VisionServiceOuterClass.Feature.newBuilder().setType(VisionServiceOuterClass.Feature.Type.FACE_DETECTION).build())
                        .setContent(ByteString.copyFrom(photoData))
                        .build())
                .build();
        VisionServiceOuterClass.BatchAnalyzeResponse response = visionService.batchAnalyze(request);
        VisionServiceOuterClass.AnalyzeResult result = response.getResults(0);
        List<VisionServiceOuterClass.FeatureResult> featureResult = result.getResultsList();
        for (VisionServiceOuterClass.FeatureResult value : featureResult) {
            List<String> cropNames = new ArrayList<>();
            List<FaceDetection.Face> facesList = value.getFaceDetection().getFacesList();
            for (int j = 0; j < facesList.size(); j++) {
                List<Primitives.Vertex> vertexList = facesList.get(j).getBoundingBox().getVerticesList();
                List<Long> x = new ArrayList<>();
                List<Long> y = new ArrayList<>();
                logger.log(Level.INFO, "{}", j);
                for (Primitives.Vertex vertex : vertexList) {
                    x.add(vertex.getX());
                    y.add(vertex.getY());
                }
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(photoData));
                BufferedImage crop = image.getSubimage(x.get(0).intValue(), y.get(0).intValue(), x.get(2).intValue() - x.get(0).intValue(), y.get(2).intValue() - y.get(0).intValue());
                x.forEach(val -> logger.log(Level.INFO, String.valueOf(value)));
                y.forEach(val -> logger.log(Level.INFO, String.valueOf(value)));
                String fileNameWithoutExtension = filename.substring(0, filename.lastIndexOf("."));
                String crops =  fileNameWithoutExtension + j + "." + JPEG_EXTENSION;
                saveToBucket(crop, crops);
                cropNames.add(crops);
            }
            linkPhotoToFace(cropNames, filename);
            sendToMessageQueue(cropNames);
        }
    }

    public void saveToBucket(BufferedImage crop, String cropName) throws IOException {
        AmazonS3 s3 = Configuration.getInstance();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(crop, JPEG_EXTENSION, os);
        InputStream is = new ByteArrayInputStream(os.toByteArray());
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(IOUtils.toByteArray(is).length);
        ByteArrayOutputStream os1 = new ByteArrayOutputStream();
        ImageIO.write(crop, JPEG_EXTENSION, os1);
        InputStream is1 = new ByteArrayInputStream(os.toByteArray());
        s3.putObject(VVOT, cropName, is1, objectMetadata);
    }

    public void linkPhotoToFace(List<String> faces, String photo) throws IOException, JSONException {
        AmazonS3 s3 = Configuration.getInstance();
        S3Object faceObject = s3.getObject(VVOT, FACES_JSON);
        S3ObjectInputStream s3ObjectInputStream = faceObject.getObjectContent();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(s3ObjectInputStream));
        String jsonText = SimpleUtil.readAll(bufferedReader);
        if (jsonText.isEmpty()) {
            JSONObject json = new JSONObject();
            json.put(photo, faces);
            s3.putObject(VVOT, FACES_JSON, json.toString());
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, List<String>> jsonMap = objectMapper.readValue(jsonText, new TypeReference<>() {
            });
            if (!jsonMap.containsKey(photo)) {
                jsonMap.put(photo, faces);
            } else {
                for (String face : faces) {
                    jsonMap.get(photo).add(face);
                }
            }
            s3.putObject(VVOT, FACES_JSON, new ObjectMapper().writeValueAsString(jsonMap));
        }
    }

    public void sendToMessageQueue(List<String> cropNames) throws JMSException {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials("*accessKey*", "*secretKey*");
        SQSConnectionFactory connectionFactory = new SQSConnectionFactory(
                new ProviderConfiguration(),
                AmazonSQSClientBuilder.standard()
                        .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                        .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                                SERVICE_ENDPOINT, SIGNING_REGION
                        ))
        );
        SQSConnection connection = connectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(QUEUE_NAME);
        MessageProducer producer = session.createProducer(queue);
        StringBuilder sb = new StringBuilder();
        for (String cropName : cropNames) {
            sb.append(cropName).append(" ");
        }
        Message message = session.createTextMessage(sb.toString());
        producer.send(message);
    }
}
