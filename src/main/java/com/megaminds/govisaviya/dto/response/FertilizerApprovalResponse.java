package com.megaminds.govisaviya.dto.response;

import com.megaminds.govisaviya.entity.enums.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO returned for a fertilizer approval record.
 * Returned to both farmers (their own records) and admins (all records).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FertilizerApprovalResponse {

    private Long id;
    private String farmerEmail;
    private String farmerName;
    private String fertilizerType;
    private String quantityRequested;
    private String landArea;
    private String purpose;
    private List<String> documentUrls;
    private ApprovalStatus status;
    private String remarks;
    private String reviewedBy;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
}
