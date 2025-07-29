package com.example.post.test.DTOs;


import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String password;
    private String phoneNumber;
    private String roles;
    private String email;
    private String address;
    private String licenseNumber;
    private String status;
    private Long carId;
    private Long regionId;
}
