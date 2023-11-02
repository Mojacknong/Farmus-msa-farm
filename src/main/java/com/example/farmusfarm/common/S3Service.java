package com.example.farmusfarm.common;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.s3.uploadPath}")
    private String uploadPath;

    public String uploadPdf(MultipartFile file, String fileType) {
        String key = fileType + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            PutObjectRequest request = new PutObjectRequest(bucketName, key, file.getInputStream(), metadata);
            s3Client.putObject(request);

            return key;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<byte[]> downloadPdf(String key, String downloadFileName) {
        if (key == null)
            return ResponseEntity.badRequest().build();

        S3Object fullObject;
        try {
            fullObject = s3Client.getObject(bucketName, key);
            if (fullObject == null)
                return ResponseEntity.badRequest().build();
        } catch (AmazonS3Exception e) {
            throw new RuntimeException(e);
        }

        try {
            S3ObjectInputStream s3is = fullObject.getObjectContent();
            byte[] bytes = IOUtils.toByteArray(s3is);

            String filename = URLEncoder.encode(downloadFileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            httpHeaders.setContentLength(bytes.length);
            httpHeaders.setContentDispositionFormData("attachment", filename);

            return ResponseEntity.ok().headers(httpHeaders).body(bytes);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String uploadImage(MultipartFile multipartFile, String imageType) {
        validateMultipartFile(multipartFile);

        String savedFileName = getSavedFileName(multipartFile, imageType);
        ObjectMetadata metadata = new ObjectMetadata();
        try(InputStream inputStream = multipartFile.getInputStream()) {
            s3Client.putObject(bucketName, savedFileName, inputStream, metadata);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return getResourceUrl(savedFileName);
    }

    private void validateMultipartFile(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }
    }

    public String getSavedFileName(MultipartFile multipartFile, String imageType) {
        String uuid = UUID.randomUUID().toString().replace("-", "");

        return String.format("%s/%s-%s", imageType, uuid, multipartFile.getOriginalFilename());
    }

    public String getResourceUrl(String savedFileName) {
        return s3Client.getResourceUrl(bucketName, savedFileName);
    }

    public String getFileNameFromResourceUrl(String fileUrl) {
        return fileUrl.replace(uploadPath + "/", "");
    }

    public String uploadVideo(MultipartFile multipartFile, String videoType) {
        validateMultipartFile(multipartFile);

        String savedFileName = getSavedFileName(multipartFile, videoType);

        // MIME 타입 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            s3Client.putObject(bucketName, savedFileName, inputStream, metadata);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return getResourceUrl(savedFileName);
    }
}
