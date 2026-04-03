package com.megaminds.govisaviya.service.impl;

import com.megaminds.govisaviya.apiclient.AgroMonitor;
import com.megaminds.govisaviya.dto.request.CreatePolygonRequest;
import com.megaminds.govisaviya.dto.response.PolygonResponse;
import com.megaminds.govisaviya.entity.PolygonRecord;
import com.megaminds.govisaviya.repository.PolygonRecordRepository;
import com.megaminds.govisaviya.service.MonitorService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * MonitorService implementation.
 * Delegates the HTTP call to {@link AgroMonitor}, persists all response fields
 * to {@code polygon_records}, and wraps the raw JSON in a {@link PolygonResponse}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MonitorServiceImpl implements MonitorService {

    private final AgroMonitor agroMonitor;
    private final PolygonRecordRepository polygonRecordRepository;
    private final ObjectMapper objectMapper;

    /**
     * {@inheritDoc}
     *
     * <p>Steps:
     * <ol>
     *   <li>POST to AgroMonitoring /polygons and receive the JSON response.</li>
     *   <li>Extract individual fields from the response.</li>
     *   <li>Resolve the authenticated user's email from the SecurityContext.</li>
     *   <li>Persist a {@link PolygonRecord} with all extracted fields.</li>
     *   <li>Return the full raw JSON wrapped in {@link PolygonResponse}.</li>
     * </ol>
     */
    @Override
    public PolygonResponse createPolygon(CreatePolygonRequest request) {
        log.info("Creating polygon '{}' for authenticated user", request.getName());

        // 1. Call AgroMonitoring API
        JsonNode agroResponse = agroMonitor.createPolygon(request);

        // 2. Parse individual fields from the response
        String agroPolygonId = agroResponse.path("id").asString(null);
        String name          = agroResponse.path("name").asString(null);
        String agroUserId    = agroResponse.path("user_id").asString(null);
        double area          = agroResponse.path("area").asDouble(0.0);

        // center: [lon, lat]
        JsonNode centerNode = agroResponse.path("center");
        Double centerLon = (!centerNode.isMissingNode() && centerNode.isArray() && centerNode.size() >= 2)
                ? centerNode.get(0).asDouble() : null;
        Double centerLat = (!centerNode.isMissingNode() && centerNode.isArray() && centerNode.size() >= 2)
                ? centerNode.get(1).asDouble() : null;

        // geo_json serialised back to a string for TEXT column storage
        String geoJsonStr;
        try {
            geoJsonStr = objectMapper.writeValueAsString(agroResponse.path("geo_json"));
        } catch (Exception e) {
            log.warn("Could not serialise geo_json to string, storing empty object", e);
            geoJsonStr = "{}";
        }

        // 3. Resolve authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = (auth != null && auth.getName() != null) ? auth.getName() : "anonymous";

        // 4. Persist to DB
        PolygonRecord record = PolygonRecord.builder()
                .userEmail(userEmail)
                .agroPolygonId(agroPolygonId)
                .name(name)
                .geoJson(geoJsonStr)
                .centerLon(centerLon)
                .centerLat(centerLat)
                .area(area)
                .agroUserId(agroUserId)
                .build();

        polygonRecordRepository.save(record);

        log.info("PolygonRecord saved — user: {}, agroId: {}, name: {}, area: {}",
                userEmail, agroPolygonId, name, area);

        // 5. Build and return response
        PolygonResponse response = new PolygonResponse();
        response.setPolygon(agroResponse);
        return response;
    }

    /** {@inheritDoc} */
    @Override
    public JsonNode getSoilData(String polygonId) {
        log.info("Fetching soil data for polygonId: {}", polygonId);
        return agroMonitor.getSoilData(polygonId);
    }

    /** {@inheritDoc} */
    @Override
    public JsonNode getWeatherData(double lat, double lon) {
        log.info("Fetching weather data for lat={}, lon={}", lat, lon);
        return agroMonitor.getWeatherData(lat, lon);
    }

    /** {@inheritDoc} */
    @Override
    public PolygonRecord getMyPolygon() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = (auth != null && auth.getName() != null) ? auth.getName() : "anonymous";

        log.info("Fetching most recent polygon record for user: {}", userEmail);

        return polygonRecordRepository.findByUserEmailOrderByCreatedAtDesc(userEmail)
                .stream()
                .findFirst()
                .orElse(null);
    }

    /** {@inheritDoc} */
    @Override
    public JsonNode getWeatherForecastData(double lat, double lon) {
        log.info("Fetching weather forecast data for lat={}, lon={}", lat, lon);
        return agroMonitor.getWeatherForecastData(lat, lon);
    }
}
