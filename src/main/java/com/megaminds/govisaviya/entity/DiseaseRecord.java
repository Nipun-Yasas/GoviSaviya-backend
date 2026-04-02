package com.megaminds.govisaviya.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Stores the result of a plant disease identification request.
 * Each record holds the submitter's email, all uploaded image URLs,
 * and the top disease name returned by the Pl@ntNet API.
 */
@Entity
@Table(name = "disease_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiseaseRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Email of the user who submitted the request. */
    @Column(nullable = false)
    private String userEmail;

    /**
     * S3 URLs of all images uploaded in the request.
     * Stored in a separate join table disease_record_images.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "disease_record_images",
            joinColumns = @JoinColumn(name = "record_id")
    )
    @Column(name = "image_url", nullable = false)
    private List<String> imageUrls;

    /**
     * Top disease identified — mapped from results[0].description
     * in the Pl@ntNet API response.
     */
    @Column(nullable = false)
    private String diseaseName;

    /** Timestamp when this identification was performed. */
    @Column(nullable = false)
    private LocalDateTime identifiedAt;

    @PrePersist
    protected void onCreate() {
        identifiedAt = LocalDateTime.now();
    }
}
