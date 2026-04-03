package com.megaminds.govisaviya.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Multipart request DTO submitted by a farmer when applying for fertilizer approval.
 * Text fields are sent as regular form-data parts; documents as file parts.
 */
@Data
public class FertilizerApprovalRequest {

    @NotBlank(message = "Fertilizer type is required")
    private String fertilizerType;

    @NotBlank(message = "Quantity requested is required")
    private String quantityRequested;

    @NotBlank(message = "Land area is required")
    private String landArea;

    private String purpose;

    /** One or more supporting documents (land deed, NIC copy, etc.). */
    private List<MultipartFile> documents;
}
