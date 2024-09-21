package com.manmeet.animalsys.controller;

import com.manmeet.animalsys.entity.Shelter;
import com.manmeet.animalsys.entity.User;
import com.manmeet.animalsys.service.ShelterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/shelters")
public class ShelterController {

    @Autowired
    private ShelterService shelterService;

    @PostMapping
    public ResponseEntity<Shelter> createShelter(@RequestBody Shelter shelter) {
        Shelter savedShelter = shelterService.saveShelter(shelter);
        return new ResponseEntity<>(savedShelter, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Shelter>> getAllShelters() {
        List<Shelter> shelters = shelterService.getAllShelters();
        return new ResponseEntity<>(shelters, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Shelter> getShelterById(@PathVariable Long id) {
        Optional<Shelter> shelter = shelterService.getShelterById(id);
        return shelter.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping
    public ResponseEntity<Shelter> updateShelter(@RequestBody Shelter shelter) {
        Shelter updatedShelter = shelterService.updateShelter(shelter);
        return new ResponseEntity<>(updatedShelter, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShelter(@PathVariable Long id) {
        shelterService.deleteShelter(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Shelter>> searchShelters(@RequestParam(required = false) String location,
                                                        @RequestParam(required = false) Integer capacity) {
        List<Shelter> shelters = shelterService.searchShelters(location, capacity);
        return new ResponseEntity<>(shelters, HttpStatus.OK);
    }

    @PatchMapping("/{id}/capacity/increase")
    public ResponseEntity<Shelter> increaseCapacity(@PathVariable Long id, @RequestParam int increment) {
        Shelter updatedShelter = shelterService.increaseCapacity(id, increment);
        return new ResponseEntity<>(updatedShelter, HttpStatus.OK);
    }

    @PatchMapping("/{id}/capacity/decrease")
    public ResponseEntity<Shelter> decreaseCapacity(@PathVariable Long id, @RequestParam int decrement) {
        Shelter updatedShelter = shelterService.decreaseCapacity(id, decrement);
        return new ResponseEntity<>(updatedShelter, HttpStatus.OK);
    }

    @PostMapping("/{shelterId}/staff")
    public ResponseEntity<User> addStaffToShelter(@PathVariable Long shelterId, @RequestBody User staff) {
        User addedStaff = shelterService.addStaffToShelter(shelterId, staff);
        return new ResponseEntity<>(addedStaff, HttpStatus.CREATED);
    }

    @DeleteMapping("/{shelterId}/staff/{staffId}")
    public ResponseEntity<Void> removeStaffFromShelter(@PathVariable Long shelterId, @PathVariable Long staffId) {
        shelterService.removeStaffFromShelter(shelterId, staffId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{shelterId}/staff")
    public ResponseEntity<List<User>> getStaffByShelter(@PathVariable Long shelterId) {
        List<User> staff = shelterService.getStaffByShelter(shelterId);
        return new ResponseEntity<>(staff, HttpStatus.OK);
    }
}
