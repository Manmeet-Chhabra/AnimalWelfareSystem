package com.manmeet.animalsys.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manmeet.animalsys.entity.Adoption;
import com.manmeet.animalsys.entity.AdoptionRequestStatus;
import com.manmeet.animalsys.repos.AdoptionRepository;
import com.manmeet.animalsys.service.AdoptionService;

@Service
public class AdoptionServiceImpl implements AdoptionService {

    @Autowired
    private AdoptionRepository adoptionRepository;

    // Evaluate the score based on answers
    @Override
    @Transactional
    public int evaluateScore(List<String> answers) {
        int score = 0;
        for (String answer : answers) {
            if (answer.equalsIgnoreCase("yes")) {
                score += 10;
            } else if (answer.equalsIgnoreCase("no")) {
                score += 1;
            }
        }
        return score;
    }

    // Create and return the adoption request
    @Override
    @Transactional
    public Adoption createAdoptionRequest(Adoption adoption) {
        return adoptionRepository.save(adoption);
    }

    // Retrieve all adoption requests
    @Override
    @Transactional
    public List<Adoption> getAllAdoptionRequests() {
        return adoptionRepository.findAll();
    }

    // Retrieve a specific adoption request by ID
    @Override
    @Transactional
    public Adoption getAdoptionRequestById(Long id) {
        return adoptionRepository.findById(id).orElse(null);
    }

    // Update an adoption request
    @Override
    @Transactional
    public void updateAdoptionRequest(Long id, Adoption adoption) {
        if (adoptionRepository.existsById(id)) {
            adoption.setId(id); // Ensure correct record is updated
            adoptionRepository.save(adoption);
        }
    }
    
    @Override
    @Transactional
    public Adoption updateRequestStatus(Long requestId, AdoptionRequestStatus status) {
        Adoption adoption = adoptionRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Adoption request not found")); // Optional: Handle request not found

        adoption.setStatus(status); // Update status
        return adoptionRepository.save(adoption); // Save updated request
    }

    // Delete an adoption request
    @Override
    @Transactional
    public void deleteAdoptionRequest(Long id) {
        adoptionRepository.deleteById(id);
    }
    
    @Override
    @Transactional
    public List<Adoption> getAdoptionsByUserId(Long userId) {
        return adoptionRepository.findByUserId(userId);
    }
}
