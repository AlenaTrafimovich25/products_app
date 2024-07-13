package com.andersen.products_app.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {

  String putS3Object(String objectKey,
                     MultipartFile multipartFile);

  void deleteFile(String fileName);
}
