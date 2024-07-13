package com.andersen.products_app.integration_tests.s3client;

import static com.andersen.utils.Constants.AWS_BUCKET_NAME;
import static com.andersen.utils.Constants.FILE_CONTENT;
import static com.andersen.utils.Constants.FILE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Testcontainers
public final class S3IntegrationTest {

  private S3Client s3Client;

  @Container
  public static LocalStackContainer localStackContainer = new LocalStackContainer(
      DockerImageName.parse("localstack/localstack:1.3"))
      .withServices(LocalStackContainer.Service.S3);

  @BeforeEach
  void setUp() {
    s3Client = S3Client.builder()
        .endpointOverride(localStackContainer.getEndpointOverride(LocalStackContainer.Service.S3))
        .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(
            localStackContainer.getAccessKey(), localStackContainer.getSecretKey())))
        .region(Region.of(localStackContainer.getRegion()))
        .build();

    s3Client.createBucket(CreateBucketRequest.builder().bucket(AWS_BUCKET_NAME).build());
  }

  @Test
  void whenPutObject_thenObjectSaved() throws IOException {
    s3Client.putObject(
        PutObjectRequest.builder().bucket(AWS_BUCKET_NAME).key(FILE_NAME).build(),
        RequestBody.fromBytes(FILE_CONTENT.getBytes(StandardCharsets.UTF_8))
    );

    var response = s3Client.getObject(
        GetObjectRequest.builder().bucket(AWS_BUCKET_NAME).key(FILE_NAME).build()
    );

    byte[] contentBytes = response.readAllBytes();
    String downloadedContent = new String(contentBytes, StandardCharsets.UTF_8);

    assertEquals(FILE_CONTENT, downloadedContent);
  }

  @Test
  void whenDeleteObject_thenObjectDeleted() {
    s3Client.putObject(
        PutObjectRequest.builder().bucket(AWS_BUCKET_NAME).key(FILE_NAME).build(),
        RequestBody.fromBytes(FILE_CONTENT.getBytes(StandardCharsets.UTF_8))
    );

    var deleteObjectRequest = DeleteObjectRequest.builder()
        .bucket(AWS_BUCKET_NAME)
        .key(FILE_NAME)
        .build();

    s3Client.deleteObject(deleteObjectRequest);

    assertThrows(S3Exception.class, () -> {
      s3Client.getObject(
          GetObjectRequest.builder()
              .bucket(AWS_BUCKET_NAME)
              .key(FILE_NAME)
              .build()
      );
    });
  }
}
