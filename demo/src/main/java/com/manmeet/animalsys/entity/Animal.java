package com.manmeet.animalsys.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "animals")
public class Animal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type; // e.g., cat, dog, etc.

    @Column(nullable = false)
    private String healthStatus;

    @Column
    private String doctorAppointmentDetails;

    // Additional fields as needed

    // Relationships (e.g., many-to-one with Shelter) can be added here
}