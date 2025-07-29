package com.example.post.test.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;


@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String middleName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String roles;

    @Column(nullable = false, unique = true)
    private String email;

    private String address;

    @OneToMany(mappedBy = "reporter")
    private List<Emergency> emergencies;

    // Driver-specific fields (now part of User for DRIVER role)
    private String licenseNumber;
    private String status; // e.g., "FREE", "ON_PROGRESS"
    private Double currentLatitude;
    private Double currentLongitude;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    @ManyToOne
    @JoinColumn(name = "region_id")
    private Region region;
}
