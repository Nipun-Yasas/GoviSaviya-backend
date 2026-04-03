package com.megaminds.govisaviya.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.megaminds.govisaviya.service.S3Service;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;


    /**
     * Uploads a single MultipartFile to S3 under the given folder prefix.
     *
     * @param file   the image file to upload
     * @param folder the S3 folder/prefix (e.g. "disease-images")
     * @return the public HTTPS URL of the uploaded object
     */
    public String uploadImage(MultipartFile file, String folder) {
        String originalFilename = file.getOriginalFilename();
        String extension = (originalFilename != null && originalFilename.contains("."))
                ? originalFilename.substring(originalFilename.lastIndexOf('.'))
                : ".jpg";

        String key = folder + "/" + UUID.randomUUID() + extension;

        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));

            String url = "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;
            return url;

        } catch (IOException e) {
            log.error("[S3] IOException reading file bytes for '{}': {}", originalFilename, e.getMessage());
            throw new RuntimeException("Failed to read file bytes for S3 upload: " + originalFilename, e);
        } catch (S3Exception e) {
            log.error("[S3] S3Exception uploading '{}': HTTP {} — {} (bucket={}, region={})",
                    originalFilename, e.statusCode(), e.awsErrorDetails().errorMessage(), bucketName, region);
            throw new RuntimeException("S3 upload failed [" + e.statusCode() + "]: " + e.awsErrorDetails().errorMessage(), e);
        } catch (Exception e) {
            log.error("[S3] Unexpected error uploading '{}': {} — {}", originalFilename, e.getClass().getSimpleName(), e.getMessage());
            throw new RuntimeException("Failed to upload image to S3: " + originalFilename, e);
        }
    }


    /**
     * Uploads all provided images to S3 and returns the list of public URLs.
     *
     * @param files  list of image files
     * @param folder the S3 folder/prefix
     * @return ordered list of public URLs corresponding to each uploaded file
     */
    public List<String> uploadImages(List<MultipartFile> files, String folder) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            urls.add(uploadImage(file, folder));
        }
        return urls;
    }
}
