package com.andersen.products_app.config;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

  @Value("${amazon.aws.accesskey}")
  private String accessKey;

  @Value("${amazon.aws.secretkey}")
  private String secretKey;

  @Value("${amazon.aws.region}")
  private String region;

  @Bean
  @ConditionalOnProperty(name = "amazon.aws.endpoint")
  public S3Client localS3Client(@Value("${amazon.aws.endpoint}") String endpoint) {
    return S3Client.builder()
        .region(Region.of(region))
        .endpointOverride(URI.create(endpoint))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)))
        .build();
  }

  @Bean
  @ConditionalOnProperty(name = "amazon.aws.endpoint", matchIfMissing = true)
  public S3Client remoteS3Client() {
    return S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)))
        .build();
  }
}
