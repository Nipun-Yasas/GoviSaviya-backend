package com.megaminds.govisaviya.apiclient;

import com.megaminds.govisaviya.dto.request.CreatePolygonRequest;
import com.megaminds.govisaviya.exception.ExternalServiceException;
import com.megaminds.govisaviya.util.RestURIs;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

/**
 * API client for the AgroMonitoring REST API.
 * All calls are routed through this component; business logic lives in the service layer.
 */
@Slf4j
@Component
public class AgroMonitor {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${agro.api.key}")
    private String apiKey;

    public AgroMonitor(
            @Value("${agro.api.url}") String baseUrl,
            RestClient.Builder restClientBuilder,
            ObjectMapper objectMapper) {
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .build();
        this.objectMapper = objectMapper;
    }

    /**
     * Creates a new polygon in AgroMonitoring.
     *
     * <p>POST {agro.api.url}/polygons?appid={apiKey}
     * <p>The request body wraps the incoming coordinates inside the required GeoJSON Feature envelope.
     *
     * @param request DTO containing the polygon name and outer-ring coordinates
     * @return Raw JSON response from AgroMonitoring (contains id, name, geo_json, area, etc.)
     */
    public JsonNode createPolygon(CreatePolygonRequest request) {

        // Build the GeoJSON Feature body expected by AgroMonitoring
        ObjectNode body = objectMapper.createObjectNode();
        body.put("name", request.getName());

        ObjectNode geoJson = objectMapper.createObjectNode();
        geoJson.put("type", "Feature");
        geoJson.putObject("properties");   // empty properties object

        ObjectNode geometry = objectMapper.createObjectNode();
        geometry.put("type", "Polygon");

        // coordinates: [ [ [lon,lat], [lon,lat], ... ] ]  – outer ring wrapped in an extra array
        ArrayNode outerRing = objectMapper.createArrayNode();
        for (List<Double> pair : request.getCoordinates()) {
            ArrayNode point = objectMapper.createArrayNode();
            point.add(pair.get(0)); // longitude
            point.add(pair.get(1)); // latitude
            outerRing.add(point);
        }
        ArrayNode coordinates = objectMapper.createArrayNode();
        coordinates.add(outerRing);
        geometry.set("coordinates", coordinates);

        geoJson.set("geometry", geometry);
        body.set("geo_json", geoJson);

        log.debug("AgroMonitor createPolygon request body: {}", body);

        try {
            return restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path(RestURIs.AGRO_POLYGONS)
                            .queryParam("appid", apiKey)
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientException e) {
            log.error("AgroMonitoring createPolygon API call failed: {}", e.getMessage(), e);
            throw new ExternalServiceException("AgroMonitoring createPolygon API call failed", e);
        }
    }

    /**
     * Fetches the latest soil data for a given polygon.
     *
     * <p>GET {agro.api.url}/soil?polyid={polygonId}&appid={apiKey}
     *
     * @param polygonId AgroMonitoring polygon ID
     * @return Raw JSON: dt, t10 (K), moisture (m³/m³), t0 (K)
     */
    public JsonNode getSoilData(String polygonId) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(RestURIs.AGRO_SOIL)
                            .queryParam("polyid", polygonId)
                            .queryParam("appid", apiKey)
                            .build())
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientException e) {
            log.error("AgroMonitoring getSoilData API call failed for polyid={}: {}", polygonId, e.getMessage(), e);
            throw new ExternalServiceException("AgroMonitoring getSoilData API call failed", e);
        }
    }

    /**
     * Fetches current weather data for the given coordinates.
     *
     * <p>GET {agro.api.url}/weather?lat={lat}&lon={lon}&appid={apiKey}
     *
     * @param lat Latitude of the location
     * @param lon Longitude of the location
     * @return Raw JSON: dt, weather[], main, wind, clouds, etc.
     */
    public JsonNode getWeatherData(double lat, double lon) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(RestURIs.AGRO_WEATHER)
                            .queryParam("lat", lat)
                            .queryParam("lon", lon)
                            .queryParam("appid", apiKey)
                            .build())
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientException e) {
            log.error("AgroMonitoring getWeatherData API call failed for lat={}, lon={}: {}", lat, lon, e.getMessage(), e);
            throw new ExternalServiceException("AgroMonitoring getWeatherData API call failed", e);
        }
    }
}
