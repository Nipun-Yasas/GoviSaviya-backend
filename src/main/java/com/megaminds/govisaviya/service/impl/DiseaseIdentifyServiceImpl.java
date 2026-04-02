package com.megaminds.govisaviya.service.impl;

import com.megaminds.govisaviya.apiclient.PlantNet;
import com.megaminds.govisaviya.dto.response.IdentifiedDisease;
import com.megaminds.govisaviya.service.DiseaseIdentifyService;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.JsonNode;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiseaseIdentifyServiceImpl implements DiseaseIdentifyService {

    private final PlantNet plantNetClient;

    /**
     * Delegates disease identification to the Pl@ntNet API client,
     * then uploads the first image to S3 and attaches the URL to the response.
     *
     * @param images List of plant images to analyze (max 5)
     * @return Parsed Pl@ntNet disease identification response with S3 image URL
     */
    @Override
    public IdentifiedDisease identifyDisease(List<MultipartFile> images) {
        JsonNode response = plantNetClient.identifyDisease(images);

        IdentifiedDisease identifiedDisease = new IdentifiedDisease<>();
        identifiedDisease.setResults(response);
        return identifiedDisease;
    }
}
