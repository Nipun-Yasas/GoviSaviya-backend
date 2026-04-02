package com.megaminds.govisaviya.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface S3Service {

    /**
     * Uploads a single MultipartFile to S3 under the given folder prefix.
     *
     * @param file   the image file to upload
     * @param folder the S3 folder/prefix (e.g. "disease-images")
     * @return the public HTTPS URL of the uploaded object
     */
    String uploadImage(MultipartFile file, String folder);

    /**
     * Uploads all provided images to S3 and returns the list of public URLs.
     *
     * @param files  list of image files
     * @param folder the S3 folder/prefix
     * @return ordered list of public URLs corresponding to each uploaded file
     */
    List<String> uploadImages(List<MultipartFile> files, String folder);
}
