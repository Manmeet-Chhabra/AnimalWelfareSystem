package com.manmeet.animalsys.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.manmeet.animalsys.entity.AdoptionStatus;
import com.manmeet.animalsys.entity.Animal;
import com.manmeet.animalsys.entity.Shelter;
import com.manmeet.animalsys.repos.AdoptionRepository;
import com.manmeet.animalsys.repos.AnimalRepository;
import com.manmeet.animalsys.repos.ShelterRepository;
import com.manmeet.animalsys.service.AnimalService;

@Service
public class AnimalServiceImpl implements AnimalService {

	@Autowired
	private AnimalRepository animalRepository;

	@Autowired
	private ShelterRepository shelterRepository;

	@Override
	public Animal addAnimal(Long shelterId, Animal animal) {
		Optional<Shelter> shelterOptional = shelterRepository.findById(shelterId);
		if (shelterOptional.isPresent()) {
			animal.setShelter(shelterOptional.get());
			return animalRepository.save(animal);
		}
		throw new IllegalArgumentException("Shelter with id " + shelterId + " does not exist.");
	}

	@Override
	public List<Animal> getAnimalsByShelter(Long shelterId) {
		return animalRepository.findByShelter_Id(shelterId);
	}

	@Override
	public Animal saveAnimal(Animal animal) {
		return animalRepository.save(animal);
	}

	@Override
	public void deleteAnimal(Long id) {
		animalRepository.deleteById(id);
	}

	@Override
	public Optional<Animal> getAnimalById(Long id) {
		return animalRepository.findById(id);
	}

	@Override
	public List<Animal> getAllAnimals() {
		return animalRepository.findAll();
	}

	@Override
	public List<Animal> searchAnimals(String type, String healthStatus) {
		// Implement search logic here based on type and health status
		return animalRepository.findByTypeAndHealthStatus(type, healthStatus);
	}

	@Override
	public Animal updateAnimal(Long id, Animal animal) {
		// Check if the animal exists
		Optional<Animal> existingAnimal = animalRepository.findById(id);
		if (existingAnimal.isPresent()) {
			// Update fields as necessary
			animal.setId(id);
			return animalRepository.save(animal);
		} else {
			throw new IllegalArgumentException("Animal with ID " + id + " does not exist.");
		}
	}

	@Override
	public List<Animal> getAvailableAnimals() {
		// Fetch and return animals with status AVAILABLE
		return animalRepository.findByAdoptionStatus(AdoptionStatus.AVAILABLE);
	}

}
