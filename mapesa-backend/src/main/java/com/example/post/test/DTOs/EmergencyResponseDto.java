package com.example.post.test.DTOs;

import lombok.Data;

@Data
public class EmergencyResponseDto {
    private EmergencyDto emergency;
    private UserDto driver; // Changed from DriverDto to UserDto
    private UserDto reporter;
    private UserDto dispatcher; // Added dispatcher
    private CarDto car;
}
