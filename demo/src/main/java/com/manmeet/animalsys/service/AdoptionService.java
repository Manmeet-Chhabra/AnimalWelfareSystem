package com.manmeet.animalsys.service;

import java.util.List;

import com.manmeet.animalsys.entity.Adoption;
import com.manmeet.animalsys.entity.AdoptionAnswer;
import com.manmeet.animalsys.entity.AdoptionRequestStatus;

public interface AdoptionService {
    // Method to create adoption request
    Adoption createAdoptionRequest(Adoption adoption);

    // Method to retrieve all adoption requests
    List<Adoption> getAllAdoptionRequests();

    // Method to get a specific adoption request by ID
    Adoption getAdoptionRequestById(Long id);

    // Method to update an adoption request
    void updateAdoptionRequest(Long id, Adoption adoption);

    // Method to delete an adoption request
    void deleteAdoptionRequest(Long id);

    // Method to evaluate eligibility score
    int evaluateScore(List<String> answers);

    // Method to update request status
    Adoption updateRequestStatus(Long requestId, AdoptionRequestStatus status);

    // Method to get adoptions by user ID
    List<Adoption> getAdoptionsByUserId(Long userId);

    // Method to save adoption answers
    void saveAdoptionAnswers(Adoption adoption, List<String> answers);

    // Method to get adoption answers
    List<AdoptionAnswer> getAdoptionAnswers(Long adoptionId);

    // Method to save adoption
    Adoption save(Adoption adoption);
}
