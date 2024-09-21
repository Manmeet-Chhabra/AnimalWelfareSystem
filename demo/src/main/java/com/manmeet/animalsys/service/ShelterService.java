package com.manmeet.animalsys.service;

import com.manmeet.animalsys.entity.User;
import com.manmeet.animalsys.entity.Shelter;

import java.util.List;
import java.util.Optional;

public interface ShelterService {

    Shelter saveShelter(Shelter shelter);

    List<Shelter> getAllShelters();

    Optional<Shelter> getShelterById(Long id);

    void deleteShelter(Long id);

    Shelter updateShelter(Shelter shelter);

    List<Shelter> searchShelters(String location, Integer capacity);

    Shelter increaseCapacity(Long shelterId, int increment);

    Shelter decreaseCapacity(Long shelterId, int decrement);

    // New methods for managing staff
    User addStaffToShelter(Long shelterId, User staff);

    void removeStaffFromShelter(Long shelterId, Long staffId);

    List<User> getStaffByShelter(Long shelterId);
}
