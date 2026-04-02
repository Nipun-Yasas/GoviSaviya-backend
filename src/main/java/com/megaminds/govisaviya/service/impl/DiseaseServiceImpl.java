package com.megaminds.govisaviya.service.impl;

import com.megaminds.govisaviya.apiclient.PlantNet;
import com.megaminds.govisaviya.dto.response.DiseaseHistory;
import com.megaminds.govisaviya.dto.response.IdentifiedDisease;
import com.megaminds.govisaviya.entity.DiseaseRecord;
import com.megaminds.govisaviya.repository.DiseaseRecordRepository;
import com.megaminds.govisaviya.service.DiseaseService;
import com.megaminds.govisaviya.service.S3Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.JsonNode;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiseaseServiceImpl implements DiseaseService {

    private final PlantNet plantNetClient;
    private final S3Service s3Service;
    private final DiseaseRecordRepository diseaseRecordRepository;

    /**
     * Calls Pl@ntNet to identify diseases, uploads all images to S3,
     * persists the record to PostgreSQL, and returns the enriched response.
     *
     * @param images List of plant images to analyze (max 5)
     * @return Identified disease result with all S3 image URLs attached
     */
    @Override
    public IdentifiedDisease identifyDisease(List<MultipartFile> images) {

        // 1. Call Pl@ntNet API
        JsonNode apiResponse = plantNetClient.identifyDisease(images);

        // 2. Upload all images to S3
        List<String> imageUrls = s3Service.uploadImages(images, "disease-images");

        // 3. Extract disease name → result.results[0].description
        JsonNode descriptionNode = apiResponse.path("results").path(0).path("description");
        String diseaseName = (!descriptionNode.isMissingNode() && !descriptionNode.isNull())
                ? descriptionNode.asString()
                : "Unknown";

        // 4. Get authenticated user email from SecurityContext
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = (auth != null) ? auth.getName() : "anonymous";

        // 5. Persist to PostgreSQL
        DiseaseRecord record = DiseaseRecord.builder()
                .userEmail(userEmail)
                .imageUrls(imageUrls)
                .diseaseName(diseaseName)
                .build();
        diseaseRecordRepository.save(record);

        log.info("Disease record saved — user: {}, disease: {}, images: {}", userEmail, diseaseName, imageUrls.size());

        // 6. Build and return response
        IdentifiedDisease<JsonNode> response = new IdentifiedDisease<>();
        response.setImageUrls(imageUrls);
        response.setResult(apiResponse);
        return response;
    }

    @Override
    public List<DiseaseHistory> getDiseaseHistory() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = (auth != null) ? auth.getName() : "anonymous";
        List<DiseaseRecord> records = diseaseRecordRepository.findByUserEmailOrderByIdentifiedAtDesc(userEmail);
        return records.stream().map(record -> new DiseaseHistory(record.getDiseaseName(), record.getImageUrls())).collect(Collectors.toList());
    }
}
