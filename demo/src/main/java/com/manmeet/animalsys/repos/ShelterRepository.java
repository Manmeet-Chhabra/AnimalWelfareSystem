package com.manmeet.animalsys.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.manmeet.animalsys.entity.Shelter;

@Repository
public interface ShelterRepository extends JpaRepository<Shelter, Long> {

	
	// Find shelters by location and capacity
    List<Shelter> findByLocationAndCapacity(String location, Integer capacity);
    
    // Find shelters by location
    List<Shelter> findByLocation(String location);
    
    // Find shelters with capacity greater than or equal to specified value
    List<Shelter> findByCapacityGreaterThanEqual(Integer capacity);
    
    // Custom query to find shelters ordered by capacity
    @Query("SELECT s FROM Shelter s ORDER BY s.capacity DESC")
    List<Shelter> findAllOrderByCapacity();
}
