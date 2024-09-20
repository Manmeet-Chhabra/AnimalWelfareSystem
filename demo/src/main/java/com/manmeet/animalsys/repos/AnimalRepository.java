package com.manmeet.animalsys.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manmeet.animalsys.entity.Animal;

public interface AnimalRepository extends JpaRepository<Animal, Long> {
}
