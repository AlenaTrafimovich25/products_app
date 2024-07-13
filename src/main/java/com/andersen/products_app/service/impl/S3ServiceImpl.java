package com.andersen.products_app.service.impl;

import com.andersen.products_app.exception.S3DeleteObjectException;
import com.andersen.products_app.exception.S3PutObjectException;
import com.andersen.products_app.service.S3Service;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class S3ServiceImpl implements S3Service {

  @Value("${amazon.aws.bucketName}")
  private String awsBucketName;

  private final S3Client s3Client;

  private static final Logger LOGGER = LoggerFactory.getLogger(S3ServiceImpl.class);

  public S3ServiceImpl(S3Client s3Client) {
    this.s3Client = s3Client;
  }

  @Override
  public String putS3Object(String objectKey,
                            MultipartFile multipartFile) {
    var putObjectRequest = PutObjectRequest.builder()
        .bucket(awsBucketName)
        .key(objectKey)
        .build();
    try {
      s3Client.putObject(putObjectRequest, RequestBody.fromBytes(multipartFile.getBytes()));
      LOGGER.info("Successfully placed [{}] into bucket ",
          putObjectRequest.key());
    } catch (S3Exception | IOException e) {
      LOGGER.error("Error during put s3 object with key [{}], message: [{}]",
          putObjectRequest.key(), e.getMessage());
      throw new S3PutObjectException(e.getMessage());
    }
    return s3Client.utilities().
        getUrl(GetUrlRequest.builder()
            .bucket(awsBucketName)
            .key(objectKey)
            .build())
        .toString();
  }

  @Override
  public void deleteFile(String fileName) {
    var deleteObjectRequest = DeleteObjectRequest.builder()
        .bucket(awsBucketName)
        .key(fileName.replaceAll(".+/", ""))
        .build();
    try {
      s3Client.deleteObject(deleteObjectRequest);
      LOGGER.info("Content [{}] was successfully deleted", fileName);
    } catch (S3Exception e) {
      LOGGER.error("Error during deletion of s3 object with key [{}], message: [{}]",
          deleteObjectRequest.key(), e.getMessage());
      throw new S3DeleteObjectException(e.getMessage());
    }
  }
}
