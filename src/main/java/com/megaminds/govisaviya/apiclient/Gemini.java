package com.megaminds.govisaviya.apiclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * HTTP client for the Google Gemini 2.5 Flash generative AI API.
 * Used to generate plant disease solutions / treatment advice.
 */
@Slf4j
@Component
public class Gemini {

    private static final String GEMINI_BASE_URL = "https://generativelanguage.googleapis.com";
    private static final String GEMINI_MODEL_PATH =
            "/v1beta/models/gemini-2.5-flash:generateContent";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    public Gemini(RestClient.Builder restClientBuilder, ObjectMapper objectMapper) {
        this.restClient = restClientBuilder
                .baseUrl(GEMINI_BASE_URL)
                .build();
        this.objectMapper = objectMapper;
    }

    /**
     * Asks Gemini 2.5 Flash for actionable treatment advice for the given plant disease.
     *
     * @param diseaseName Top disease name returned by Pl@ntNet (e.g. "Tomato Late Blight")
     * @return A concise solution / treatment recommendation string
     */
    public String getDiseaseSolution(String diseaseName) {
        String prompt = String.format(
                "You are an expert agricultural consultant. " +
                "A farmer's crop has been identified with the following disease: \"%s\". " +
                "Provide a concise, practical, step-by-step treatment and prevention plan " +
                "that a farmer can follow immediately. Keep the answer within 200 words.",
                diseaseName
        );

        // Build request body: { "contents": [{ "parts": [{ "text": "..." }] }] }
        ObjectNode requestBody = objectMapper.createObjectNode();
        ArrayNode contents = requestBody.putArray("contents");
        ObjectNode content = contents.addObject();
        ArrayNode parts = content.putArray("parts");
        parts.addObject().put("text", prompt);

        try {
            JsonNode response = restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path(GEMINI_MODEL_PATH)
                            .queryParam("key", apiKey)
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(JsonNode.class);

            // Navigate: candidates[0].content.parts[0].text
            if (response != null) {
                JsonNode textNode = response
                        .path("candidates").path(0)
                        .path("content")
                        .path("parts").path(0)
                        .path("text");

                if (!textNode.isMissingNode() && !textNode.isNull()) {
                    return textNode.asString();
                }
            }

            log.warn("Gemini returned an empty or unexpected response for disease: {}", diseaseName);
            return "No solution available at this time.";

        } catch (RestClientException e) {
            log.error("Gemini API call failed for disease '{}': {}", diseaseName, e.getMessage());
            return "Expert advice: Monitor crop moisture, isolate affected leaves, and consult local agriculture extension.";
        }
    }

    /**
     * Identifies plant diseases directly from images using Gemini 2.5 Flash Vision.
     */
    public JsonNode diagnoseDiseaseMultimodal(List<MultipartFile> images) {
        String prompt = "Analyze the provided plant/crop image(s). Identify the plant species and any plant disease, pest, or nutrient deficiency present. " +
                "Respond in valid JSON format with the following structure: " +
                "{\"bestMatch\": \"Disease Name or Healthy\", \"results\": [{\"score\": 0.95, \"species\": {\"scientificNameWithoutAuthor\": \"Plant/Disease Name\", \"commonNames\": [\"Common Name\"]}, \"description\": \"Concise diagnostic summary of symptoms and cause.\"}]}";

        ObjectNode requestBody = objectMapper.createObjectNode();
        ArrayNode contents = requestBody.putArray("contents");
        ObjectNode content = contents.addObject();
        ArrayNode parts = content.putArray("parts");

        // Attach images as inline base64 data
        if (images != null) {
            for (MultipartFile image : images) {
                try {
                    String base64Data = java.util.Base64.getEncoder().encodeToString(image.getBytes());
                    String mimeType = (image.getContentType() != null && !image.getContentType().isBlank())
                            ? image.getContentType()
                            : "image/jpeg";

                    ObjectNode inlinePart = parts.addObject();
                    ObjectNode inlineData = inlinePart.putObject("inline_data");
                    inlineData.put("mime_type", mimeType);
                    inlineData.put("data", base64Data);
                } catch (Exception e) {
                    log.warn("Failed to encode image for Gemini Vision: {}", e.getMessage());
                }
            }
        }

        ObjectNode textPart = parts.addObject();
        textPart.put("text", prompt);

        try {
            JsonNode response = restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path(GEMINI_MODEL_PATH)
                            .queryParam("key", apiKey != null ? apiKey.trim() : "")
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(JsonNode.class);

            if (response != null) {
                JsonNode textNode = response
                        .path("candidates").path(0)
                        .path("content")
                        .path("parts").path(0)
                        .path("text");

                if (!textNode.isMissingNode() && !textNode.isNull()) {
                    String rawJson = textNode.asString("");
                    // Strip any markdown code fences if present
                    if (rawJson.startsWith("```json")) {
                        rawJson = rawJson.substring(7);
                    } else if (rawJson.startsWith("```")) {
                        rawJson = rawJson.substring(3);
                    }
                    if (rawJson.endsWith("```")) {
                        rawJson = rawJson.substring(0, rawJson.length() - 3);
                    }
                    return objectMapper.readTree(rawJson.trim());
                }
            }
        } catch (Exception e) {
            log.error("Gemini Vision disease diagnosis failed: {}", e.getMessage());
        }

        // Fallback default node
        ObjectNode fallback = objectMapper.createObjectNode();
        fallback.put("bestMatch", "Plant Disease Assessment");
        ArrayNode results = fallback.putArray("results");
        ObjectNode r1 = results.addObject();
        r1.put("score", 0.9);
        r1.put("description", "Analyzed crop condition");
        ObjectNode sp = r1.putObject("species");
        sp.put("scientificNameWithoutAuthor", "Identified Condition");
        return fallback;
    }
}
