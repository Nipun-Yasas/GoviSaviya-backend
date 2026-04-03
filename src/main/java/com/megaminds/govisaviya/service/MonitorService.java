package com.megaminds.govisaviya.service;

import com.megaminds.govisaviya.dto.request.CreatePolygonRequest;
import com.megaminds.govisaviya.dto.response.PolygonResponse;
import com.megaminds.govisaviya.entity.PolygonRecord;

import tools.jackson.databind.JsonNode;

/**
 * Service contract for AgroMonitoring operations.
 */
public interface MonitorService {

    /**
     * Creates a monitoring polygon via the AgroMonitoring API.
     *
     * @param request DTO containing polygon name and coordinates
     * @return {@link PolygonResponse} wrapping the raw AgroMonitoring JSON
     */
    PolygonResponse createPolygon(CreatePolygonRequest request);

    /**
     * Returns the latest soil data for a polygon.
     *
     * @param polygonId AgroMonitoring polygon ID
     * @return Raw JSON: dt, t10 (K), moisture (m³/m³), t0 (K)
     */
    JsonNode getSoilData(String polygonId);

    /**
     * Returns current weather data for a geographic coordinate.
     *
     * @param lat Latitude
     * @param lon Longitude
     * @return Raw JSON: dt, weather[], main, wind, clouds, etc.
     */
    JsonNode getWeatherData(double lat, double lon);

    /**
     * Retrieves the most recent polygon created by the authenticated user.
     * @return The polygon record or null if none exists.
     */
    PolygonRecord getMyPolygon();

    /**
     * Returns weather forecast data for a geographic coordinate.
     *
     * @param lat Latitude
     * @param lon Longitude
     * @return Raw JSON array: dt, weather[], main, wind, clouds, etc.
     */
    JsonNode getWeatherForecastData(double lat, double lon);
}
