package com.megaminds.govisaviya.dto.response;

import lombok.Data;

/**
 * Top-level response DTO for the Pl@ntNet disease identification API.
 * Maps to the JSON response structure from POST /v2/diseases/identify.
 *
 * @param <T> Type of the results list (typically a JsonNode or List<DiseaseResult>)
 */
@Data
public class IdentifiedDisease<T> {

    /** Full disease identification results from Pl@ntNet. */
    private T results;

}
