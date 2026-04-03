package com.megaminds.govisaviya.controller;

import com.megaminds.govisaviya.dto.request.FertilizerApprovalRequest;
import com.megaminds.govisaviya.dto.request.ReviewApprovalRequest;
import com.megaminds.govisaviya.dto.response.FertilizerApprovalResponse;
import com.megaminds.govisaviya.entity.enums.ApprovalStatus;
import com.megaminds.govisaviya.service.FertilizerApprovalService;
import com.megaminds.govisaviya.util.RestURIs;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST controller for the fertilizer approval system.
 *
 * Farmer endpoints  → /api/v1/fertilizer/**  (role: FARMER)
 * Admin endpoints   → /api/v1/fertilizer/admin/**  (role: ADMIN)
 */
@RestController
@RequestMapping(RestURIs.FERTILIZER)
@RequiredArgsConstructor
public class FertilizerApprovalController {

    private final FertilizerApprovalService fertilizerApprovalService;

    // ─────────────────────────────────────────────────────────────────────────
    // FARMER ENDPOINTS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * POST /api/v1/fertilizer/submit
     * Farmer submits a new fertilizer request with optional supporting documents.
     * Accepts multipart/form-data.
     */
    @PostMapping(value = RestURIs.FERTILIZER_SUBMIT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<FertilizerApprovalResponse> submitRequest(
            @RequestParam("fertilizerType")    String fertilizerType,
            @RequestParam("quantityRequested") String quantityRequested,
            @RequestParam("landArea")          String landArea,
            @RequestParam(value = "purpose", required = false) String purpose,
            @RequestParam(value = "documents", required = false) List<MultipartFile> documents) {

        FertilizerApprovalRequest request = new FertilizerApprovalRequest();
        request.setFertilizerType(fertilizerType);
        request.setQuantityRequested(quantityRequested);
        request.setLandArea(landArea);
        request.setPurpose(purpose);
        request.setDocuments(documents);

        FertilizerApprovalResponse response = fertilizerApprovalService.submitRequest(request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/fertilizer/my-requests
     * Farmer views their own past and current requests.
     */
    @GetMapping(RestURIs.FERTILIZER_MY_REQUESTS)
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<List<FertilizerApprovalResponse>> getMyRequests() {
        return ResponseEntity.ok(fertilizerApprovalService.getMyRequests());
    }

    /**
     * GET /api/v1/fertilizer/{id}
     * Retrieve details of a specific fertilizer request.
     * Farmers can only view their own requests; Admins can view any.
     */
    @GetMapping(RestURIs.FERTILIZER_BY_ID)
    @PreAuthorize("hasAnyRole('FARMER', 'ADMIN')")
    public ResponseEntity<FertilizerApprovalResponse> getRequestDetails(@PathVariable Long id) {
        return ResponseEntity.ok(fertilizerApprovalService.getRequestById(id));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ADMIN ENDPOINTS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * GET /api/v1/fertilizer/admin/all?status=PENDING
     * Admin retrieves all requests, optionally filtered by status.
     */
    @GetMapping(RestURIs.FERTILIZER_ADMIN_ALL)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FertilizerApprovalResponse>> getAllRequests(
            @RequestParam(value = "status", required = false) ApprovalStatus status) {
        return ResponseEntity.ok(fertilizerApprovalService.getAllRequests(status));
    }

    /**
     * GET /api/v1/fertilizer/admin/{id}
     * Admin retrieves a single request by ID.
     */
    @GetMapping(RestURIs.FERTILIZER_ADMIN_BY_ID)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FertilizerApprovalResponse> getRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(fertilizerApprovalService.getRequestById(id));
    }

    /**
     * PATCH /api/v1/fertilizer/admin/{id}/review
     * Admin approves or rejects a request and optionally adds remarks.
     */
    @PatchMapping(RestURIs.FERTILIZER_ADMIN_REVIEW)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FertilizerApprovalResponse> reviewRequest(
            @PathVariable Long id,
            @Valid @RequestBody ReviewApprovalRequest review) {
        return ResponseEntity.ok(fertilizerApprovalService.reviewRequest(id, review));
    }
}
