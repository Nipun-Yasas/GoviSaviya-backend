package com.megaminds.govisaviya.apiclient;

import com.megaminds.govisaviya.exception.ExternalServiceException;
import com.megaminds.govisaviya.util.RestURIs;

import tools.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class PlantNet {


    // Fixed defaults as requested
    private static final boolean INCLUDE_RELATED_IMAGES = false;
    private static final boolean NO_REJECT = false;
    private static final int NB_RESULTS = 5;
    private static final String LANG = "en";
    private static final String DEFAULT_ORGAN = "auto";

    private final RestClient restClient;

    @Value("${plantnet.api.key}")
    private String apiKey;

    public PlantNet(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl(RestURIs.PLANTNET)
                .build();
    }

    /**
     * Identifies plant diseases from a list of uploaded images.
     * Fixed defaults: include-related-images=false, no-reject=false, nb-results=5, lang=en, organs=auto.
     *
     * @param images List of image files to analyze (max 5)
     * @return IdentifiedDisease response parsed from the Pl@ntNet API
     */
    public JsonNode identifyDisease(List<MultipartFile> images) {
        MultiValueMap<String, HttpEntity<?>> multipartData = new LinkedMultiValueMap<>();

        for (MultipartFile image : images) {
            try {
                HttpHeaders imageHeaders = new HttpHeaders();
                imageHeaders.setContentType(MediaType.IMAGE_JPEG);
                imageHeaders.setContentDispositionFormData("images", image.getOriginalFilename());
                multipartData.add("images", new HttpEntity<>(image.getBytes(), imageHeaders));
            } catch (IOException e) {
                throw new ExternalServiceException("Failed to read image bytes: " + image.getOriginalFilename(), e);
            }
            // One "auto" organ per image (must match image count)
            multipartData.add("organs", new HttpEntity<>(DEFAULT_ORGAN));
        }

        try {
            return restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path(RestURIs.DISEASE_IDENTIFY)
                            .queryParam("include-related-images", INCLUDE_RELATED_IMAGES)
                            .queryParam("no-reject", NO_REJECT)
                            .queryParam("nb-results", NB_RESULTS)
                            .queryParam("lang", LANG)
                            .queryParam("api-key", apiKey != null ? apiKey.trim() : "")
                            .build())
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(multipartData)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientException e) {
            log.error("Pl@ntNet API call failed: {}", e.getMessage(),e);
            throw new ExternalServiceException("Pl@ntNet API call failed", e);
        }
    }
}
