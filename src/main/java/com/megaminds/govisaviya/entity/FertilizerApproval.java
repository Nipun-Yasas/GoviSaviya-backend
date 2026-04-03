package com.megaminds.govisaviya.entity;

import com.megaminds.govisaviya.entity.enums.ApprovalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a fertilizer subsidy / approval request submitted by a farmer.
 * Documents are stored in S3; only the URLs are persisted here.
 */
@Entity
@Table(name = "fertilizer_approvals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FertilizerApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Email of the farmer who submitted the request (from JWT). */
    @Column(nullable = false)
    private String farmerEmail;

    /** Farmer full name for convenience. */
    @Column(nullable = false)
    private String farmerName;

    /** Type/name of fertilizer requested. */
    @Column(nullable = false)
    private String fertilizerType;

    /** Quantity requested (e.g. "50 kg"). */
    @Column(nullable = false)
    private String quantityRequested;

    /** Land area the fertilizer is needed for (e.g. "2 acres"). */
    @Column(nullable = false)
    private String landArea;

    /** Purpose / reason for requesting the fertilizer. */
    @Column(columnDefinition = "TEXT")
    private String purpose;

    /**
     * S3 URLs of supporting documents (e.g. land deed, NIC copy, etc.).
     * Stored in a separate join table.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "fertilizer_approval_documents",
            joinColumns = @JoinColumn(name = "approval_id")
    )
    @Column(name = "document_url", nullable = false)
    private List<String> documentUrls;

    /** Current approval status. Defaults to PENDING on creation. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ApprovalStatus status = ApprovalStatus.PENDING;

    /**
     * Remarks added by the admin when approving / rejecting.
     * Also useful for requesting additional information.
     */
    @Column(columnDefinition = "TEXT")
    private String remarks;

    /** Email of the admin who reviewed this request. */
    @Column
    private String reviewedBy;

    /** Timestamp when the request was submitted. */
    @Column(nullable = false, updatable = false)
    private LocalDateTime submittedAt;

    /** Timestamp when the admin last reviewed (approved/rejected) the request. */
    @Column
    private LocalDateTime reviewedAt;

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
    }
}
