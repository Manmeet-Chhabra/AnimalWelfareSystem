package com.manmeet.animalsys.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    

        @Query("SELECT s FROM Shelter s WHERE " +
               "(:name IS NULL OR s.name LIKE %:name%) AND " +
               "(:location IS NULL OR s.location LIKE %:location%) AND " +
               "(:capacity IS NULL OR s.capacity = :capacity)")
        List<Shelter> findSheltersByFilters(@Param("name") String name, 
                                            @Param("location") String location, 
                                            @Param("capacity") Integer capacity);
    

}
