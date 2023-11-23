package com.example.pmproject.Util;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        File uploadFile = convert(multipartFile).orElseThrow(() -> new IllegalStateException("파일 전환 실패"));
        return upload(uploadFile, dirName);
    }

    public void deleteFile(String deleteFile, String dirName) throws IOException {
        String fileName = dirName + "/" + deleteFile;
        try {
            amazonS3Client.deleteObject(bucket, fileName);
        } catch (SdkClientException e) {
            throw new IOException("Error delete file from S3", e);
        }
    }

    private String upload(File uploadFile, String dirName) {
        String newFileName = UUID.randomUUID() + uploadFile.getName();
        String fileName = dirName + "/" + newFileName;
        String uploadImageUrl = putS3(uploadFile, fileName);
        removeNewFile(uploadFile);

        return newFileName;
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));

        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("File delete success");
            return;
        }
        log.info("File delete fail");
    }

    private Optional<File> convert(MultipartFile multipartFile) throws IOException {
        System.out.println(System.getProperty("user.dir"));
        File convertFile = new File(System.getProperty("user.dir") + "/" + multipartFile.getOriginalFilename());

        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(multipartFile.getBytes());
            }
            return Optional.of(convertFile);
        }

        return Optional.empty();

    }
}
