package com.manmeet.animalsys.service;

import java.util.List;
import java.util.Optional;

import com.manmeet.animalsys.entity.AdoptionStatus;
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

	


	Animal findByName(String name);

	Animal save(Animal animal);

	long getTotalAnimals();

	List<Animal> getAnimalsByType(String type);

	List<Animal> searchAnimals(String type, String healthStatus, AdoptionStatus adoptionStatus, Long shelterId,
			String doctorAppointment);
}
