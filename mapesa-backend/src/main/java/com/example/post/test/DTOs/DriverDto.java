package com.example.post.test.DTOs;

import lombok.Data;

@Data
public class DriverDto {
    private Long id;
    private UserDto user; // Represents the associated user details
    private String licenseNumber;
    private String availabilityStatus;
    private Double currentLatitude;
    private Double currentLongitude;
}
