package com.megaminds.govisaviya.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import tools.jackson.databind.JsonNode;

/**
 * Generic response DTO returned to the frontend after creating a polygon
 * via the AgroMonitoring API.
 */
@Data
public class PolygonResponse {

    /**
     * Raw JSON response body returned by the AgroMonitoring /polygons endpoint.
     * Preserves all fields (id, name, geo_json, area, etc.) without tight coupling.
     */
    @JsonProperty("polygon")
    private JsonNode polygon;
}
