package com.example.post.test.DTOs;

import com.example.post.test.enums.Status;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmergencyDto {
    private Long id;
    private String description;
    private Double latitude;
    private Double longitude;
    private String locationDescription;
    private Status status;
    private LocalDateTime reportedAt;
    private Long reporterId;
    private Long dispatcherId;
    private Long driverId; // Keep this for now, as it might be used for assignment
}
