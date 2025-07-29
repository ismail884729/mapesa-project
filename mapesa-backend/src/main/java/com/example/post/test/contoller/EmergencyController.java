package com.example.post.test.contoller;

import com.example.post.test.DTOs.EmergencyDto;
import com.example.post.test.DTOs.EmergencyResponseDto;
import com.example.post.test.DTOs.UserDto; // Import UserDto
import com.example.post.test.entity.Emergency;
import com.example.post.test.entity.User; // Import User entity
import com.example.post.test.service.EmergencyService;
import com.example.post.test.service.UserService;
import lombok.Data; // Ensure this is imported
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/emergencies")
@Data
@Tag(name = "Emergency Management", description = "APIs for managing emergencies")
public class EmergencyController {
    @Autowired
    private EmergencyService emergencyService;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    public EmergencyController(EmergencyService emergencyService, UserService userService, ModelMapper modelMapper) {
        this.emergencyService = emergencyService;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<EmergencyDto> createEmergency(@RequestBody EmergencyDto dto) {
        System.out.println("Received emergency: " + dto);
        Emergency emergency = modelMapper.map(dto, Emergency.class);
        Emergency saved = emergencyService.saveEmergency(emergency);
        return new ResponseEntity<>(modelMapper.map(saved, EmergencyDto.class), HttpStatus.CREATED);
    }
//    @PostMapping("/ema")
//    public ResponseEntity<EmergencyDto> receiveEmergency(@RequestBody EmergencyDto emerge) {
//        System.out.println("Received emergency from: " + emerge.getLocationDescription());
//
//        Emergency emergency = modelMapper.map(emerge, Emergency.class);
//
//        // Set the reporter based on the ID from the DTO
//        if (emerge.getReporterId() != null) {
//            User reporter = UserService.findById(emerge.getReporterId()); // Assume you have a userService to fetch the user
//            emergency.setReporter(reporter);
//        } else {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // or handle it as needed
//        }
//
//        Emergency saved = emergencyService.saveEmergency(emergency);
//        return new ResponseEntity<>(modelMapper.map(saved, EmergencyDto.class), HttpStatus.CREATED);
//    }


    @GetMapping
    public ResponseEntity<List<EmergencyDto>> getAllEmergencies() {
        List<Emergency> emergencies = emergencyService.getAllEmergencies();
        List<EmergencyDto> result = emergencies.stream().map(e -> modelMapper.map(e, EmergencyDto.class)).collect(Collectors.toList());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<EmergencyResponseDto>> getEmergencies() {
        return new ResponseEntity<>(emergencyService.getEmergencies(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmergencyDto> getEmergencyById(@PathVariable Long id) {
        Optional<Emergency> emergency = emergencyService.getEmergencyById(id);
        return emergency.map(e -> new ResponseEntity<>(modelMapper.map(e, EmergencyDto.class), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmergencyDto> updateEmergency(@PathVariable Long id, @RequestBody EmergencyDto dto) {
        if (!emergencyService.isExist(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        dto.setId(id);
        Emergency updated = modelMapper.map(dto, Emergency.class);
        Emergency saved = emergencyService.saveEmergency(updated);
        return new ResponseEntity<>(modelMapper.map(saved, EmergencyDto.class), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmergency(@PathVariable Long id) {
        emergencyService.deleteEmergency(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/driver")
    public ResponseEntity<UserDto> getDriverForEmergency(@PathVariable Long id) {
        Optional<Emergency> emergencyOpt = emergencyService.getEmergencyById(id);
        if (emergencyOpt.isPresent() && emergencyOpt.get().getDriver() != null) {
            return new ResponseEntity<>(modelMapper.map(emergencyOpt.get().getDriver(), UserDto.class), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{emergencyId}/approve/{dispatcherId}")
    public ResponseEntity<EmergencyDto> approveEmergency(@PathVariable Long emergencyId, @PathVariable Long dispatcherId) {
        try {
            Emergency approvedEmergency = emergencyService.approveEmergency(emergencyId, dispatcherId);
            return new ResponseEntity<>(modelMapper.map(approvedEmergency, EmergencyDto.class), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{emergencyId}/reject/{dispatcherId}")
    public ResponseEntity<EmergencyDto> rejectEmergency(@PathVariable Long emergencyId, @PathVariable Long dispatcherId) {
        try {
            Emergency rejectedEmergency = emergencyService.rejectEmergency(emergencyId, dispatcherId);
            return new ResponseEntity<>(modelMapper.map(rejectedEmergency, EmergencyDto.class), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{emergencyId}/assign/{driverId}/{dispatcherId}")
    public ResponseEntity<EmergencyDto> assignEmergencyToDriver(@PathVariable Long emergencyId, @PathVariable Long driverId, @PathVariable Long dispatcherId) {
        try {
            Emergency assignedEmergency = emergencyService.assignEmergencyToDriver(emergencyId, driverId, dispatcherId);
            return new ResponseEntity<>(modelMapper.map(assignedEmergency, EmergencyDto.class), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{emergencyId}/complete/{driverId}")
    public ResponseEntity<EmergencyDto> completeEmergency(@PathVariable Long emergencyId, @PathVariable Long driverId) {
        try {
            Emergency completedEmergency = emergencyService.completeEmergency(emergencyId, driverId);
            return new ResponseEntity<>(modelMapper.map(completedEmergency, EmergencyDto.class), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/drivers/{driverId}/location")
    public ResponseEntity<UserDto> updateDriverLocation(@PathVariable Long driverId, @RequestParam Double latitude, @RequestParam Double longitude) {
        try {
            User updatedDriver = emergencyService.updateDriverLocation(driverId, latitude, longitude);
            return new ResponseEntity<>(modelMapper.map(updatedDriver, UserDto.class), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/driver/{driverId}/assigned")
    public ResponseEntity<List<EmergencyDto>> getAssignedEmergencies(@PathVariable Long driverId) {
        List<Emergency> emergencies = emergencyService.getEmergenciesByDriver(driverId);
        List<EmergencyDto> result = emergencies.stream().map(e -> modelMapper.map(e, EmergencyDto.class)).collect(Collectors.toList());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/reporter/{emergencyId}/driver")
    public ResponseEntity<UserDto> getAssignedDriverForReporter(@PathVariable Long emergencyId) {
        Optional<Emergency> emergencyOpt = emergencyService.getEmergencyById(emergencyId);
        if (emergencyOpt.isPresent() && emergencyOpt.get().getDriver() != null) {
            return new ResponseEntity<>(modelMapper.map(emergencyOpt.get().getDriver(), UserDto.class), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/reporter/{reporterId}")
    public ResponseEntity<List<EmergencyDto>> getEmergenciesByReporter(@PathVariable Long reporterId) {
        List<Emergency> emergencies = emergencyService.getEmergenciesByReporter(reporterId);
        List<EmergencyDto> result = emergencies.stream().map(e -> modelMapper.map(e, EmergencyDto.class)).collect(Collectors.toList());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
