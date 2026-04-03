package com.megaminds.govisaviya.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Request DTO for creating a monitoring polygon.
 * The frontend sends a name and a list of coordinate pairs (lon, lat).
 */
@Data
public class CreatePolygonRequest {

    /** Human-readable name for the polygon. */
    @JsonProperty("name")
    private String name;

    /**
     * Outer ring coordinates as [longitude, latitude] pairs.
     * The first and last coordinate must be the same to close the ring.
     * Example: [[-121.1958, 37.6683], [-121.1779, 37.6687], ...]
     */
    @JsonProperty("coordinates")
    private List<List<Double>> coordinates;
}
