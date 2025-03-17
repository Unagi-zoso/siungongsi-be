package org.bob.siungongsi.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.zip.ZipInputStream;

import org.bob.siungongsi.config.AwsCredentialsProperties;
import org.bob.siungongsi.config.S3Properties;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

@Service
public class FileService {

  private final AmazonS3 s3Client;
  private final S3Properties s3Properties;

  private static final String s3KeyPrefix = "gongsi_file/";

  public FileService(AwsCredentialsProperties awsCredentialsProperties, S3Properties s3Properties) {
    this.s3Properties = s3Properties;

    BasicAWSCredentials awsCreds =
        new BasicAWSCredentials(
            awsCredentialsProperties.accessKey(), awsCredentialsProperties.secretKey());

    this.s3Client =
        AmazonS3ClientBuilder.standard()
            .withRegion(s3Properties.region())
            .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
            .build();
  }

  public String uploadFile(String fileName, InputStream inputStream, long contentLength) {
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(contentLength);

    String bucketName = s3Properties.bucket();
    s3Client.putObject(new PutObjectRequest(bucketName, fileName, inputStream, metadata));

    return s3Client.getUrl(bucketName, fileName).toString();
  }

  public S3Object downloadFile(String fileName) {
    return s3Client.getObject(s3Properties.bucket(), fileName);
  }

  public void deleteFile(String fileName) {
    s3Client.deleteObject(new DeleteObjectRequest(s3Properties.bucket(), fileName));
  }

  public String generateS3Key(String fileName) {
    String now = LocalDate.now().toString().replace("-", "");
    return s3KeyPrefix + now + "/" + fileName;
  }

  public boolean isZipFile(byte[] file) {
    try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(file))) {
      return zipInputStream.getNextEntry() != null;
    } catch (IOException e) {
      return false;
    }
  }
}
