package com.manmeet.animalsys.repos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.manmeet.animalsys.dto.AnimalCountDto;
import com.manmeet.animalsys.dto.AnimalReportDto;
import com.manmeet.animalsys.entity.AdoptionStatus;
import com.manmeet.animalsys.entity.Animal;

@Repository

	public interface AnimalRepository extends JpaRepository<Animal, Long>, JpaSpecificationExecutor<Animal> {
	

	// Find animals by their type
	List<Animal> findByType(String type);

	// Find animals by their health status
	List<Animal> findByHealthStatus(String healthStatus);

	// Find animals by type and health status
	List<Animal> findByTypeAndHealthStatus(String type, String healthStatus);

	// Find animals by name containing a substring
	List<Animal> findByNameContaining(String name);

	// Find animals by shelter ID
	List<Animal> findByShelter_Id(Long shelterId);

	// Find animals by adoption status
	List<Animal> findByAdoptionStatus(AdoptionStatus status);

	// Find animal by ID
	Optional<Animal> findById(Long id);

	// Find animal by name
	Animal findByName(String name);

	// New method to find a list of animals by name
	List<Animal> findAllByName(String name);

	// Custom query to count animals by type
	@Query("SELECT new com.manmeet.animalsys.dto.AnimalCountDto(a.type, COUNT(a)) " + "FROM Animal a GROUP BY a.type")
	List<AnimalCountDto> countAnimalsByType();

	// Custom query to count animals by health status
	@Query("SELECT new com.manmeet.animalsys.dto.AnimalReportDto(a.name, a.type, a.healthStatus, s.name, a.doctorAppointment) "
			+ "FROM Animal a JOIN a.shelter s WHERE a.healthStatus = :healthStatus")
	List<AnimalReportDto> findAnimalsByHealthStatus(@Param("healthStatus") String healthStatus);

	@Query("SELECT a FROM Animal a WHERE a.type = :type")
	List<Animal> findAnimalsByType(@Param("type") String type);

	@Query("SELECT COUNT(a) FROM Animal a WHERE a.healthStatus = :healthStatus")
	long countByHealthStatus(@Param("healthStatus") String healthStatus);

	@Query("SELECT a FROM Animal a WHERE " + 
		       "(:type IS NULL OR a.type = :type) AND " + 
		       "(:healthStatus IS NULL OR a.healthStatus = :healthStatus) AND " + 
		       "(:adoptionStatus IS NULL OR a.adoptionStatus = :adoptionStatus) AND " + 
		       "(:shelterId IS NULL OR a.shelter.id = :shelterId) AND " + 
		       "(:doctorAppointment IS NULL OR a.doctorAppointment = :doctorAppointment)")
		List<Animal> findAnimalsByFilters(@Param("type") String type, 
		                                  @Param("healthStatus") String healthStatus,
		                                  @Param("adoptionStatus") AdoptionStatus adoptionStatus, 
		                                  @Param("shelterId") Long shelterId,
		                                  @Param("doctorAppointment") String doctorAppointment);



}