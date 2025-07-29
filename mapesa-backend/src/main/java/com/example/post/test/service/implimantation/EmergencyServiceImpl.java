package com.example.post.test.service.implimantation;

import com.example.post.test.DTOs.*;
import com.example.post.test.entity.Emergency;
import com.example.post.test.entity.User;
import com.example.post.test.enums.Status;
import com.example.post.test.repository.EmergencyRepository;
import com.example.post.test.repository.UserRepository;
import com.example.post.test.service.EmergencyService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmergencyServiceImpl implements EmergencyService {

    private final EmergencyRepository emergencyRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public EmergencyServiceImpl(EmergencyRepository emergencyRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.emergencyRepository = emergencyRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Emergency saveEmergency(Emergency emergency) {
        return emergencyRepository.save(emergency);
    }

    @Override
    public List<Emergency> getAllEmergencies() {
        return emergencyRepository.findAll();
    }

    @Override
    public List<EmergencyResponseDto> getEmergencies() {
        List<Emergency> emergencies = emergencyRepository.findAll();
        List<EmergencyResponseDto> emergencyResponseDtoList = new ArrayList<>();
        for (Emergency emergency : emergencies) {
            EmergencyResponseDto emergencyResponseDto = new EmergencyResponseDto();
            emergencyResponseDto.setEmergency(modelMapper.map(emergency, EmergencyDto.class));
            emergencyResponseDto.setReporter(modelMapper.map(emergency.getReporter(), UserDto.class));

            if (emergency.getDriver() != null) {
                emergencyResponseDto.setDriver(modelMapper.map(emergency.getDriver(), UserDto.class)); // Map to UserDto
                if (emergency.getDriver().getCar() != null) { // Assuming getCar() is now directly on User
                    emergencyResponseDto.setCar(modelMapper.map(emergency.getDriver().getCar(), CarDto.class));
                }
            }

            if (emergency.getDispatcher() != null) {
                emergencyResponseDto.setDispatcher(modelMapper.map(emergency.getDispatcher(), UserDto.class));
            }
            emergencyResponseDtoList.add(emergencyResponseDto);
        }
        return emergencyResponseDtoList;
    }

    @Override
    public Optional<Emergency> getEmergencyById(Long id) {
        return emergencyRepository.findById(id);
    }

    @Override
    public void deleteEmergency(Long id) {
        emergencyRepository.deleteById(id);
    }

    @Override
    public boolean isExist(Long id) {
        return emergencyRepository.existsById(id);
    }

    @Override
    @Transactional
    public Emergency approveEmergency(Long emergencyId, Long dispatcherId) {
        Emergency emergency = emergencyRepository.findById(emergencyId)
                .orElseThrow(() -> new RuntimeException("Emergency not found with id: " + emergencyId));
        User dispatcher = userRepository.findById(dispatcherId)
                .orElseThrow(() -> new RuntimeException("Dispatcher not found with id: " + dispatcherId));

        emergency.setStatus(Status.APPROVED);
        emergency.setDispatcher(dispatcher);
        emergency.setRespondedAt(LocalDateTime.now());
        return emergencyRepository.save(emergency);
    }

    @Override
    @Transactional
    public Emergency rejectEmergency(Long emergencyId, Long dispatcherId) {
        Emergency emergency = emergencyRepository.findById(emergencyId)
                .orElseThrow(() -> new RuntimeException("Emergency not found with id: " + emergencyId));
        User dispatcher = userRepository.findById(dispatcherId)
                .orElseThrow(() -> new RuntimeException("Dispatcher not found with id: " + dispatcherId));

        emergency.setStatus(Status.REJECTED);
        emergency.setDispatcher(dispatcher);
        emergency.setRespondedAt(LocalDateTime.now());
        return emergencyRepository.save(emergency);
    }

    @Override
    @Transactional
    public Emergency assignEmergencyToDriver(Long emergencyId, Long driverId, Long dispatcherId) {
        Emergency emergency = emergencyRepository.findById(emergencyId)
                .orElseThrow(() -> new RuntimeException("Emergency not found with id: " + emergencyId));
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver (User) not found with id: " + driverId));
        User dispatcher = userRepository.findById(dispatcherId)
                .orElseThrow(() -> new RuntimeException("Dispatcher not found with id: " + dispatcherId));

        // Optional: Add a check to ensure the assigned user has the DRIVER role
        // if (!driver.getRoles().contains(Roles.DRIVER.name())) {
        //     throw new RuntimeException("User with id " + driverId + " is not a driver.");
        // }

        emergency.setStatus(Status.ASSIGNED);
        emergency.setDriver(driver); // Assuming Emergency entity's driver field is now User type
        emergency.setDispatcher(dispatcher);
        emergency.setRespondedAt(LocalDateTime.now()); // Or a new field like assignedAt
        return emergencyRepository.save(emergency);
    }

    @Override
    @Transactional
    public Emergency completeEmergency(Long emergencyId, Long driverId) {
        Emergency emergency = emergencyRepository.findById(emergencyId)
                .orElseThrow(() -> new RuntimeException("Emergency not found with id: " + emergencyId));
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver (User) not found with id: " + driverId));

        if (emergency.getDriver() == null || !emergency.getDriver().getId().equals(driver.getId())) {
            throw new RuntimeException("Emergency is not assigned to this driver.");
        }

        emergency.setStatus(Status.COMPLETED);
        emergency.setCompletedAt(LocalDateTime.now());
        return emergencyRepository.save(emergency);
    }

    @Override
    @Transactional
    public User updateDriverLocation(Long driverId, Double latitude, Double longitude) {
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver (User) not found with id: " + driverId));
        driver.setCurrentLatitude(latitude);
        driver.setCurrentLongitude(longitude);
        return userRepository.save(driver);
    }

    @Override
    public List<Emergency> getEmergenciesByDriver(Long driverId) {
        return emergencyRepository.findByDriverId(driverId);
    }

    @Override
    public List<Emergency> getEmergenciesByReporter(Long reporterId) {
        return emergencyRepository.findByReporterId(reporterId);
    }
}
