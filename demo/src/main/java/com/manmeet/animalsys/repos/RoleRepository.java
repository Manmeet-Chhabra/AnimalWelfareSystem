package com.manmeet.animalsys.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manmeet.animalsys.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}