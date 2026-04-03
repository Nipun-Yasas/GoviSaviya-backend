package com.megaminds.govisaviya.service;

import com.megaminds.govisaviya.dto.request.FertilizerApprovalRequest;
import com.megaminds.govisaviya.dto.request.ReviewApprovalRequest;
import com.megaminds.govisaviya.dto.response.FertilizerApprovalResponse;
import com.megaminds.govisaviya.entity.enums.ApprovalStatus;

import java.util.List;

public interface FertilizerApprovalService {

    /**
     * Farmer submits a new fertilizer approval request.
     * Documents are uploaded to S3; metadata is saved to the DB.
     *
     * @param request multipart form data from the farmer
     * @return the saved approval record as a response DTO
     */
    FertilizerApprovalResponse submitRequest(FertilizerApprovalRequest request);

    /**
     * Returns all fertilizer requests submitted by the currently authenticated farmer.
     *
     * @return list of the farmer's own requests
     */
    List<FertilizerApprovalResponse> getMyRequests();

    /**
     * Admin: retrieve all requests, optionally filtered by status.
     *
     * @param status optional filter; pass null to return everything
     * @return list of matching requests
     */
    List<FertilizerApprovalResponse> getAllRequests(ApprovalStatus status);

    /**
     * Admin: retrieve a single request by its ID.
     *
     * @param id the approval record ID
     * @return the matching approval record
     */
    FertilizerApprovalResponse getRequestById(Long id);

    /**
     * Admin: approve or reject a fertilizer request and optionally add remarks.
     *
     * @param id     the approval record ID to review
     * @param review DTO containing the new status and optional remarks
     * @return the updated approval record
     */
    FertilizerApprovalResponse reviewRequest(Long id, ReviewApprovalRequest review);
}
