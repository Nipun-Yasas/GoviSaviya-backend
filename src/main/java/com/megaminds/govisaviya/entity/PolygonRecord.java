package com.megaminds.govisaviya.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Persists a monitoring polygon created via the AgroMonitoring API.
 * Fields mirror the API response exactly plus the local user's email.
 */
@Entity
@Table(name = "polygon_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolygonRecord {

    /** Auto-generated local DB identifier. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Email of the authenticated user who created this polygon. */
    @Column(nullable = false)
    private String userEmail;

    /** Polygon ID assigned by AgroMonitoring (e.g. "5abb9fb82c8897000bde3e87"). */
    @Column(name = "agro_polygon_id", nullable = false, unique = true)
    private String agroPolygonId;

    /** Human-readable polygon name from AgroMonitoring. */
    @Column(nullable = false)
    private String name;

    /**
     * GeoJSON Feature string representing the polygon geometry.
     * Stored as TEXT to accommodate arbitrarily large coordinate rings.
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String geoJson;

    /**
     * Centroid of the polygon: [longitude, latitude].
     * Stored as two separate columns for easy querying.
     */
    @Column(name = "center_lon")
    private Double centerLon;

    @Column(name = "center_lat")
    private Double centerLat;

    /** Polygon area in hectares as returned by AgroMonitoring. */
    @Column
    private Double area;

    /**
     * AgroMonitoring's own user_id for the account that owns the polygon.
     * Kept for reference; different from our local {@link #userEmail}.
     */
    @Column(name = "agro_user_id")
    private String agroUserId;

    /** Timestamp when this record was persisted locally. */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
