package com.example.post.test.service;

import com.example.post.test.DTOs.EmergencyResponseDto;
import com.example.post.test.entity.Emergency;
import com.example.post.test.entity.User; // Import User entity
import java.util.List;
import java.util.Optional;

public interface EmergencyService {
    Emergency saveEmergency(Emergency emergency);
    List<Emergency> getAllEmergencies();

    List<EmergencyResponseDto> getEmergencies();

    Optional<Emergency> getEmergencyById(Long id);
    void deleteEmergency(Long id);
    boolean isExist(Long id);

    Emergency approveEmergency(Long emergencyId, Long dispatcherId);
    Emergency rejectEmergency(Long emergencyId, Long dispatcherId);
    Emergency assignEmergencyToDriver(Long emergencyId, Long driverId, Long dispatcherId);
    Emergency completeEmergency(Long emergencyId, Long driverId);
    User updateDriverLocation(Long driverId, Double latitude, Double longitude); // Changed return type to User
    List<Emergency> getEmergenciesByDriver(Long driverId);
    List<Emergency> getEmergenciesByReporter(Long reporterId);
}
