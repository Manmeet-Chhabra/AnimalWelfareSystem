package com.manmeet.animalsys.repos;

import com.manmeet.animalsys.entity.AdoptionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdoptionAnswerRepository extends JpaRepository<AdoptionAnswer, Long> {
    List<AdoptionAnswer> findByAdoptionId(Long adoptionId);
}
