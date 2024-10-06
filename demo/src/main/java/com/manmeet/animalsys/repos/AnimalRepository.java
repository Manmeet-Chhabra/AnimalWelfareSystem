package com.manmeet.animalsys.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.manmeet.animalsys.entity.Animal;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {

	List<Animal> findByType(String type); // Method to find animals by their type

	List<Animal> findByHealthStatus(String healthStatus); // Method to find animals by their health status

	List<Animal> findByTypeAndHealthStatus(String type, String healthStatus);

	List<Animal> findByNameContaining(String name);

	List<Animal> findByShelter_Id(Long shelterId);
}
