package com.andersen.products_app.unit_tests.service.impl;

import static com.andersen.utils.Constants.AWS_BUCKET_NAME;
import static com.andersen.utils.Constants.FILE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andersen.products_app.exception.S3DeleteObjectException;
import com.andersen.products_app.exception.S3PutObjectException;
import com.andersen.products_app.service.impl.S3ServiceImpl;
import java.io.IOException;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@ExtendWith(MockitoExtension.class)
class S3ServiceImplTest {
  @Mock
  private S3Client s3Client;
  @Mock
  private MultipartFile multipartFile;
  @Mock
  private S3Utilities s3Utilities;

  @InjectMocks
  private S3ServiceImpl s3Service;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(s3Service, "awsBucketName", AWS_BUCKET_NAME);
  }

  @Test
  void whenPutContent_thenSuccess() throws IOException {
    when(multipartFile.getBytes()).thenReturn("test-content" .getBytes());
    when(s3Utilities.getUrl(any(GetUrlRequest.class))).thenReturn
        (URI.create("http://test-bucket.s3.amazonaws.com/test-key").toURL());
    when(s3Client.utilities()).thenReturn(s3Utilities);

    var result = s3Service.putS3Object(FILE_NAME, multipartFile);

    assertEquals("http://test-bucket.s3.amazonaws.com/test-key", result);
    verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
  }

  @Test
  void whenPutContent_thenS3PutObjectExceptionThrown() throws IOException {
    var putObjectRequest = PutObjectRequest.builder()
        .bucket(AWS_BUCKET_NAME)
        .key(FILE_NAME)
        .build();

    when(multipartFile.getBytes()).thenReturn("test-content" .getBytes());
    when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenThrow(
        S3Exception.class);

    assertThrows(S3PutObjectException.class,
        () -> s3Service.putS3Object(FILE_NAME, multipartFile));

    verify(s3Client).putObject(refEq(putObjectRequest), any(RequestBody.class));
  }

  @Test
  void whenDeleteFile_thenSuccess() {
    var deleteObjectRequest = DeleteObjectRequest.builder()
        .bucket(AWS_BUCKET_NAME)
        .key(FILE_NAME)
        .build();

    s3Service.deleteFile(FILE_NAME);

    verify(s3Client).deleteObject(deleteObjectRequest);
  }

  @Test
  void whenDeleteContent_thenS3DeleteObjectExceptionThrown() {
    when(s3Client.deleteObject(any(DeleteObjectRequest.class))).thenThrow(S3Exception.class);

    assertThrows(S3DeleteObjectException.class, () -> s3Service.deleteFile(FILE_NAME));
  }
}
