package com.manmeet.animalsys.repos;

import com.manmeet.animalsys.entity.Shelter;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShelterRepository extends JpaRepository<Shelter, Long> {

	List<Shelter> findByLocationAndCapacity(String location, Integer capacity);
}
