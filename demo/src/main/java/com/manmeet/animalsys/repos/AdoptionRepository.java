package com.manmeet.animalsys.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.manmeet.animalsys.entity.Adoption;
import com.manmeet.animalsys.entity.AdoptionRequestStatus;

@Repository
public interface AdoptionRepository extends JpaRepository<Adoption, Long> {
	List<Adoption> findByStatus(AdoptionRequestStatus status);

	// Find all adoptions for a specific user
	List<Adoption> findByUserId(Long userId);
}
