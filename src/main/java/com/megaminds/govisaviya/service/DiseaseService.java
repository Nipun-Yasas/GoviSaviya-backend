package com.megaminds.govisaviya.service;

import com.megaminds.govisaviya.dto.response.DiseaseHistory;
import com.megaminds.govisaviya.dto.response.IdentifiedDisease;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DiseaseService {

    /**
     * Calls the Pl@ntNet API to identify plant diseases from the given images,
     * then enriches the response with a Gemini-generated solution.
     *
     * @param images List of image files (max 5)
     * @return Parsed disease identification response including treatment solution
     */
    IdentifiedDisease identifyDisease(List<MultipartFile> images);

    /**
     * Calls the Gemini 2.5 Flash API to get a treatment/solution for the given disease.
     *
     * @param diseaseName Top disease name from Pl@ntNet
     * @return Actionable treatment advice string
     */
    String getDiseaseSolution(String diseaseName);

    List<DiseaseHistory> getDiseaseHistory();
}
