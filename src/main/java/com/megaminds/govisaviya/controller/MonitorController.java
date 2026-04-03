package com.megaminds.govisaviya.controller;

import com.megaminds.govisaviya.dto.request.CreatePolygonRequest;
import com.megaminds.govisaviya.dto.response.PolygonResponse;
import com.megaminds.govisaviya.service.MonitorService;
import com.megaminds.govisaviya.util.RestURIs;

import tools.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller exposing AgroMonitoring operations.
 * Base path: /api/v1/monitor
 */
@RestController
@RequestMapping(RestURIs.MONITOR)
@RequiredArgsConstructor
public class MonitorController {

    private final MonitorService monitorService;

    /**
     * Creates a new monitoring polygon.
     *
     * POST /api/v1/monitor/polygon
     * Content-Type: application/json
     *
     * @param request JSON body with polygon name and coordinates
     * @return 200 OK with a {@link PolygonResponse} containing the AgroMonitoring result
     */
    @PostMapping(RestURIs.POLYGON)
    public ResponseEntity<PolygonResponse> createPolygon(@RequestBody CreatePolygonRequest request) {
        PolygonResponse response = monitorService.createPolygon(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Returns the latest soil data for a given polygon.
     *
     * GET /api/v1/monitor/soil?polygonId={id}
     *
     * @param polygonId AgroMonitoring polygon ID
     * @return Raw JSON: { dt, t10, moisture, t0 }
     */
    @GetMapping(RestURIs.SOIL)
    public ResponseEntity<JsonNode> getSoilData(@RequestParam String polygonId) {
        return ResponseEntity.ok(monitorService.getSoilData(polygonId));
    }

    /**
     * Returns current weather data for a geographic coordinate.
     *
     * GET /api/v1/monitor/weather?lat={lat}&lon={lon}
     *
     * @param lat Latitude
     * @param lon Longitude
     * @return Raw JSON: { dt, weather[], main, wind, clouds, ... }
     */
    @GetMapping(RestURIs.WEATHER)
    public ResponseEntity<JsonNode> getWeatherData(
            @RequestParam double lat,
            @RequestParam double lon) {
        return ResponseEntity.ok(monitorService.getWeatherData(lat, lon));
    }
}
