package com.manmeet.animalsys.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.manmeet.animalsys.entity.DonationGoal;

@Repository
public interface DonationGoalRepository extends JpaRepository<DonationGoal, Long> {

}
