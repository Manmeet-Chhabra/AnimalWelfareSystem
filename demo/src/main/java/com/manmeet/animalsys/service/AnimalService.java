package com.manmeet.animalsys.service;

import java.util.List;
import java.util.Optional;

import com.manmeet.animalsys.entity.Animal;

public interface AnimalService {
	
	
	List<Animal> getAvailableAnimals();

	Animal addAnimal(Long shelterId, Animal animal);

	List<Animal> getAnimalsByShelter(Long shelterId);

	Animal saveAnimal(Animal animal);

	Animal updateAnimal(Long id, Animal animal);

	void deleteAnimal(Long id);

	Optional<Animal> getAnimalById(Long id);

	List<Animal> getAllAnimals();

	List<Animal> searchAnimals(String type, String healthStatus); // Add search criteria as needed

}

