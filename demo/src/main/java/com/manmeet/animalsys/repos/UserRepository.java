package com.manmeet.animalsys.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manmeet.animalsys.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}