package org.gvm.product.gvmpoin.module.common.S3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.gvm.product.gvmpoin.module.common.exception.FileTypeNotValidException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

@Component
public class AmazonClient {

  private AmazonS3 s3client;

  @Value("${POIN_S3_ENDPOINTURL}")
  private String endPointUrl;

  @Value("${POIN_S3_BUCKETNAME}")
  private String bucketName;

  @Value("${POIN_S3_ACCESSKEY}")
  private String accessKey;

  @Value("${POIN_S3_SECRETKEY}")
  private String secretKey;

  @PostConstruct
  private void initializeAmazon() {
    AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
    this.s3client = new AmazonS3Client(credentials);
}

  public String uploadFile(MultipartFile multipartFile) {
    String fileUrl = "";
    String cloudfrontUrl = "https://d2r1w7pet7o54f.cloudfront.net";
    String mimeType = multipartFile.getContentType();
    String fileType = mimeType.split("/")[0];
    if (fileType.equalsIgnoreCase("image")) {
      try {
        File file = convertMultiPartFileToFile(multipartFile);
        String fileName = generateFileName(multipartFile);
        fileUrl = cloudfrontUrl + "/poin_core/image_profile/" + fileName;
        uploadFileToS3Bucket(fileName, file);
        file.delete();
      } catch (Exception e) {
        e.printStackTrace();
      }
      return fileUrl;
    } else {
      throw new FileTypeNotValidException();
    }

  }

  private File convertMultiPartFileToFile(MultipartFile file) throws IOException {
    File convFile = new File(file.getOriginalFilename());
    FileOutputStream fos = new FileOutputStream(convFile);
    fos.write(file.getBytes());
    fos.close();
    return convFile;
  }

  private String generateFileName(MultipartFile multipartFile) {
    return new Date().getTime() + multipartFile.getOriginalFilename().replace(" ", "_");
  }

  private void uploadFileToS3Bucket(String fileName, File file) {
    s3client.putObject(new PutObjectRequest(bucketName, fileName, file)
        .withCannedAcl(CannedAccessControlList.PublicRead));
  }

}