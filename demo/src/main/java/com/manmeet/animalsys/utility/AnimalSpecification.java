package com.manmeet.animalsys.utility;


import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.manmeet.animalsys.entity.AdoptionStatus;
import com.manmeet.animalsys.entity.Animal;

import jakarta.persistence.criteria.Predicate;

public class AnimalSpecification {

    public static Specification<Animal> filterByCriteria(String type, String healthStatus, AdoptionStatus adoptionStatus, Long shelterId, String doctorAppointment) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (type != null && !type.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }
            if (healthStatus != null && !healthStatus.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("healthStatus"), healthStatus));
            }
            if (adoptionStatus != null) {
                predicates.add(criteriaBuilder.equal(root.get("adoptionStatus"), adoptionStatus));
            }
            if (shelterId != null) {
                predicates.add(criteriaBuilder.equal(root.get("shelter").get("id"), shelterId));
            }
            if (doctorAppointment != null && !doctorAppointment.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("doctorAppointment"), doctorAppointment));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
