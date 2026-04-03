package com.megaminds.govisaviya.dto.request;

import com.megaminds.govisaviya.entity.enums.ApprovalStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request DTO used by an admin to approve or reject a fertilizer request.
 */
@Data
public class ReviewApprovalRequest {

    @NotNull(message = "Status is required (APPROVED or REJECTED)")
    private ApprovalStatus status;

    /** Optional remarks / reason for the decision. */
    private String remarks;
}
