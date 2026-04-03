package com.megaminds.govisaviya.service.impl;

import com.megaminds.govisaviya.dto.request.FertilizerApprovalRequest;
import com.megaminds.govisaviya.dto.request.ReviewApprovalRequest;
import com.megaminds.govisaviya.dto.response.FertilizerApprovalResponse;
import com.megaminds.govisaviya.entity.FertilizerApproval;
import com.megaminds.govisaviya.entity.enums.ApprovalStatus;
import com.megaminds.govisaviya.repository.FertilizerApprovalRepository;
import com.megaminds.govisaviya.service.FertilizerApprovalService;
import com.megaminds.govisaviya.service.S3Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FertilizerApprovalServiceImpl implements FertilizerApprovalService {

    private final FertilizerApprovalRepository approvalRepository;
    private final S3Service s3Service;

    // ─────────────────────────────────────────────────────────────────────────
    // FARMER OPERATIONS
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public FertilizerApprovalResponse submitRequest(FertilizerApprovalRequest request) {

        // 1. Resolve authenticated farmer
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String farmerEmail = auth.getName();
        String farmerName = (auth.getPrincipal() instanceof UserDetails ud)
                ? ud.getUsername()   // fallback – override with fullName if available
                : farmerEmail;

        // If the principal is your custom User entity you can cast and get fullName:
        if (auth.getPrincipal() instanceof com.megaminds.govisaviya.entity.User user) {
            farmerName = user.getFullName();
        }

        // 2. Upload documents to S3 (folder: fertilizer-docs)
        List<String> documentUrls = Collections.emptyList();
        if (request.getDocuments() != null && !request.getDocuments().isEmpty()) {
            List<MultipartFile> validDocs = request.getDocuments().stream()
                    .filter(f -> f != null && !f.isEmpty())
                    .collect(Collectors.toList());
            if (!validDocs.isEmpty()) {
                documentUrls = s3Service.uploadImages(validDocs, "fertilizer-docs");
            }
        }

        // 3. Build and persist the entity
        FertilizerApproval approval = FertilizerApproval.builder()
                .farmerEmail(farmerEmail)
                .farmerName(farmerName)
                .fertilizerType(request.getFertilizerType())
                .quantityRequested(request.getQuantityRequested())
                .landArea(request.getLandArea())
                .purpose(request.getPurpose())
                .documentUrls(documentUrls)
                .status(ApprovalStatus.PENDING)
                .build();

        FertilizerApproval saved = approvalRepository.save(approval);
        log.info("Fertilizer approval request #{} submitted by {}", saved.getId(), farmerEmail);

        return toResponse(saved);
    }

    @Override
    public List<FertilizerApprovalResponse> getMyRequests() {
        String farmerEmail = currentUserEmail();
        return approvalRepository
                .findByFarmerEmailOrderBySubmittedAtDesc(farmerEmail)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ADMIN OPERATIONS
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public List<FertilizerApprovalResponse> getAllRequests(ApprovalStatus status) {
        List<FertilizerApproval> records = (status != null)
                ? approvalRepository.findByStatusOrderBySubmittedAtDesc(status)
                : approvalRepository.findAllByOrderBySubmittedAtDesc();

        return records.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public FertilizerApprovalResponse getRequestById(Long id) {
        FertilizerApproval approval = approvalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fertilizer approval request not found with id: " + id));
        return toResponse(approval);
    }

    @Override
    public FertilizerApprovalResponse reviewRequest(Long id, ReviewApprovalRequest review) {

        FertilizerApproval approval = approvalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fertilizer approval request not found with id: " + id));

        if (approval.getStatus() != ApprovalStatus.PENDING) {
            throw new RuntimeException(
                    "Request #" + id + " has already been reviewed (status: " + approval.getStatus() + ")");
        }

        String adminEmail = currentUserEmail();

        approval.setStatus(review.getStatus());
        approval.setRemarks(review.getRemarks());
        approval.setReviewedBy(adminEmail);
        approval.setReviewedAt(LocalDateTime.now());

        FertilizerApproval updated = approvalRepository.save(approval);
        log.info("Fertilizer approval #{} {} by admin {}", id, review.getStatus(), adminEmail);

        return toResponse(updated);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    private String currentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : "anonymous";
    }

    private FertilizerApprovalResponse toResponse(FertilizerApproval a) {
        return FertilizerApprovalResponse.builder()
                .id(a.getId())
                .farmerEmail(a.getFarmerEmail())
                .farmerName(a.getFarmerName())
                .fertilizerType(a.getFertilizerType())
                .quantityRequested(a.getQuantityRequested())
                .landArea(a.getLandArea())
                .purpose(a.getPurpose())
                .documentUrls(a.getDocumentUrls())
                .status(a.getStatus())
                .remarks(a.getRemarks())
                .reviewedBy(a.getReviewedBy())
                .submittedAt(a.getSubmittedAt())
                .reviewedAt(a.getReviewedAt())
                .build();
    }
}
