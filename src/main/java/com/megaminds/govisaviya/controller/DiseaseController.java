package com.megaminds.govisaviya.controller;

import com.megaminds.govisaviya.dto.response.DiseaseHistory;
import com.megaminds.govisaviya.dto.response.IdentifiedDisease;
import com.megaminds.govisaviya.service.DiseaseService;
import com.megaminds.govisaviya.util.RestURIs;

import lombok.AllArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(RestURIs.DISEASE)
@AllArgsConstructor
public class DiseaseController {

    private final DiseaseService diseaseService;

    /**
     * Identifies plant diseases from uploaded images.
     * Accepts multipart/form-data with one or more image files (max 5).
     *
     * @param images List of plant image files
     * @return Disease identification result from Pl@ntNet
     */
    @PostMapping(value = RestURIs.IDENTIFY, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<IdentifiedDisease> identifyDisease(
            @RequestParam("images") List<MultipartFile> images) {

        IdentifiedDisease response = diseaseService.identifyDisease(images);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
    }

    @GetMapping(RestURIs.HISTORY)
    public ResponseEntity<List<DiseaseHistory>> getDiseaseHistory() {
        List<DiseaseHistory> response = diseaseService.getDiseaseHistory();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
    }

}
