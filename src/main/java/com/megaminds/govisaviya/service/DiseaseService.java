package com.megaminds.govisaviya.service;

import com.megaminds.govisaviya.dto.response.DiseaseHistory;
import com.megaminds.govisaviya.dto.response.IdentifiedDisease;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DiseaseService {

    /**
     * Calls the Pl@ntNet API to identify plant diseases from the given images.
     *
     * @param images List of image files (max 5)
     * @return Parsed disease identification response
     */
    IdentifiedDisease identifyDisease(List<MultipartFile> images);

    List<DiseaseHistory> getDiseaseHistory();
}
