package com.megaminds.govisaviya.service.impl;

import com.megaminds.govisaviya.apiclient.Gemini;
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
    private final Gemini geminiClient;
    private final S3Service s3Service;
    private final DiseaseRecordRepository diseaseRecordRepository;

    /**
     * Calls Pl@ntNet to identify diseases, uploads all images to S3,
     * fetches a Gemini-generated solution, persists the record,
     * and returns the enriched response.
     *
     * @param images List of plant images to analyze (max 5)
     * @return Identified disease result with S3 image URLs and treatment solution
     */
    @Override
    public IdentifiedDisease<JsonNode> identifyDisease(List<MultipartFile> images) {

        // 1. Call Pl@ntNet API with Gemini Vision fallback
        JsonNode apiResponse;
        try {
            apiResponse = plantNetClient.identifyDisease(images);
        } catch (Exception e) {
            log.warn("Pl@ntNet API unavailable or rejected ({}), falling back to Gemini AI Vision...", e.getMessage());
            apiResponse = geminiClient.diagnoseDiseaseMultimodal(images);
        }

        // 2. Upload all images to S3
        List<String> imageUrls = s3Service.uploadImages(images, "disease-images");

        // 3. Extract plant / disease name → result.results[0].description or species or bestMatch
        JsonNode firstResult = apiResponse.path("results").path(0);
        String diseaseName = "Unknown";
        if (apiResponse.hasNonNull("bestMatch") && !apiResponse.path("bestMatch").asString("").isBlank()) {
            diseaseName = apiResponse.path("bestMatch").asString("");
        } else if (!firstResult.isMissingNode() && !firstResult.isNull()) {
            if (firstResult.hasNonNull("description") && !firstResult.path("description").asString("").isBlank()) {
                diseaseName = firstResult.path("description").asString("");
            } else if (firstResult.path("species").hasNonNull("scientificNameWithoutAuthor")) {
                diseaseName = firstResult.path("species").path("scientificNameWithoutAuthor").asString("");
                if (firstResult.path("species").path("commonNames").isArray() && !firstResult.path("species").path("commonNames").isEmpty()) {
                    diseaseName += " (" + firstResult.path("species").path("commonNames").path(0).asString("") + ")";
                }
            }
        }

        // 4. Get treatment solution from Gemini 2.5 Flash
        String solution = getDiseaseSolution(diseaseName);

        // 5. Get authenticated user email from SecurityContext
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = (auth != null) ? auth.getName() : "anonymous";

        // 6. Persist to PostgreSQL (includes solution)
        DiseaseRecord record = DiseaseRecord.builder()
                .userEmail(userEmail)
                .imageUrls(imageUrls)
                .diseaseName(diseaseName)
                .solution(solution)
                .build();
        diseaseRecordRepository.save(record);

        log.info("Disease record saved — user: {}, disease: {}, images: {}",
                userEmail, diseaseName, imageUrls.size());

        // 7. Build and return enriched response
        IdentifiedDisease<JsonNode> response = new IdentifiedDisease<>();
        response.setImageUrls(imageUrls);
        response.setResult(apiResponse);
        response.setSolution(solution);
        return response;
    }

    /**
     * Delegates to GeminiClient to get a concise treatment plan for the given disease.
     *
     * @param diseaseName Top disease name from Pl@ntNet
     * @return Actionable treatment advice string from Gemini 2.5 Flash
     */
    @Override
    public String getDiseaseSolution(String diseaseName) {
        log.info("Requesting Gemini solution for disease: {}", diseaseName);
        return geminiClient.getDiseaseSolution(diseaseName);
    }

    @Override
    public List<DiseaseHistory> getDiseaseHistory() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = (auth != null) ? auth.getName() : "anonymous";
        List<DiseaseRecord> records = diseaseRecordRepository.findByUserEmailOrderByIdentifiedAtDesc(userEmail);
        return records.stream()
                .map(record -> new DiseaseHistory(record.getDiseaseName(), record.getImageUrls()))
                .collect(Collectors.toList());
    }
}
