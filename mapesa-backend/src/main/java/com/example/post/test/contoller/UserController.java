package com.example.post.test.contoller;

import com.example.post.test.DTOs.ChangePasswordRequest;
import com.example.post.test.DTOs.LoginRequest;
import com.example.post.test.DTOs.LoginResponseDto;
import com.example.post.test.DTOs.UserDto;
import com.example.post.test.entity.Car;
import com.example.post.test.entity.Region;
import com.example.post.test.entity.User;
import com.example.post.test.repository.CarRepository;
import com.example.post.test.repository.RegionRepository;
import com.example.post.test.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/user")
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private RegionRepository regionRepository;

@PostMapping
public ResponseEntity<UserDto> createUser(@RequestBody UserDto dto) {
    User user = modelMapper.map(dto, User.class);
    if (dto.getCarId() != null) {
        Car car = carRepository.findById(dto.getCarId()).orElse(null);
        user.setCar(car);
    }
    if (dto.getRegionId() != null) {
        Region region = regionRepository.findById(dto.getRegionId()).orElse(null);
        user.setRegion(region);
    }
    User saved = userService.saveUser(user);
    return new ResponseEntity<>(modelMapper.map(saved, UserDto.class), HttpStatus.CREATED);
}

@PostMapping("/driver")
public ResponseEntity<UserDto> createDriver(@RequestBody UserDto dto) {
    User user = modelMapper.map(dto, User.class);
    user.setRoles("DRIVER");
    if (dto.getCarId() != null) {
        Car car = carRepository.findById(dto.getCarId()).orElse(null);
        user.setCar(car);
    }
    if (dto.getRegionId() != null) {
        Region region = regionRepository.findById(dto.getRegionId()).orElse(null);
        user.setRegion(region);
    }
    User saved = userService.saveUser(user);
    return new ResponseEntity<>(modelMapper.map(saved, UserDto.class), HttpStatus.CREATED);
}

@PostMapping("/reporter")
public ResponseEntity<UserDto> createReporter(@RequestBody UserDto dto) {
    User user = modelMapper.map(dto, User.class);
    user.setRoles("REPORTER");
    User saved = userService.saveUser(user);
    return new ResponseEntity<>(modelMapper.map(saved, UserDto.class), HttpStatus.CREATED);
}

@PostMapping("/request")
public ResponseEntity<UserDto> createUserRequest(@RequestBody UserDto dto) {
    User user = modelMapper.map(dto, User.class);
    user.setRoles("USER");
    User saved = userService.saveUser(user);
    return new ResponseEntity<>(modelMapper.map(saved, UserDto.class), HttpStatus.CREATED);
}

@GetMapping
public ResponseEntity<List<UserDto>> getAllUsers() {
    List<User> users = userService.getAllUsers();
    List<UserDto> result = users.stream().map(u -> modelMapper.map(u, UserDto.class)).collect(Collectors.toList());
    return new ResponseEntity<>(result, HttpStatus.OK);
}

@GetMapping("/{id}")
public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
    Optional<User> user = userService.getUserById(id);
    return user.map(u -> new ResponseEntity<>(modelMapper.map(u, UserDto.class), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
}

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto dto) {
        Optional<User> existingUserOpt = userService.getUserById(id);
        if (existingUserOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        User existingUser = existingUserOpt.get();

        // Update only the fields you allow
        existingUser.setFirstName(dto.getFirstName());
        existingUser.setMiddleName(dto.getMiddleName());
        existingUser.setLastName(dto.getLastName());
        existingUser.setEmail(dto.getEmail());
        existingUser.setPhoneNumber(dto.getPhoneNumber());
        existingUser.setPassword(dto.getPassword());
        existingUser.setRoles(dto.getRoles());
        existingUser.setAddress(dto.getAddress());
        existingUser.setLicenseNumber(dto.getLicenseNumber());
        existingUser.setStatus(dto.getStatus());
        if (dto.getCarId() != null) {
            Car car = carRepository.findById(dto.getCarId()).orElse(null);
            existingUser.setCar(car);
        }
        if (dto.getRegionId() != null) {
            Region region = regionRepository.findById(dto.getRegionId()).orElse(null);
            existingUser.setRegion(region);
        }


        User updated = userService.saveUser(existingUser);
        return new ResponseEntity<>(modelMapper.map(updated, UserDto.class), HttpStatus.OK);
    }


@DeleteMapping("/{id}")
public ResponseEntity<?> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
}
    @PostMapping("/login")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDto.class)))
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        Optional<User> optionalUser = userService.findByEmailAndPassword(
                loginRequest.getEmail(), loginRequest.getPassword());

        if (optionalUser.isPresent()) {
            LoginResponseDto loginResponseDto = modelMapper.map(optionalUser.get(), LoginResponseDto.class);
            return new ResponseEntity<>(loginResponseDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/drivers/region/{regionId}")
    public ResponseEntity<List<UserDto>> getDriversByRegion(@PathVariable Long regionId) {
        List<User> drivers = userService.findDriversByRegion(regionId);
        List<UserDto> result = drivers.stream().map(d -> modelMapper.map(d, UserDto.class)).collect(Collectors.toList());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/drivers/active")
    public ResponseEntity<List<UserDto>> getActiveDrivers() {
        List<User> drivers = userService.findActiveDrivers();
        List<UserDto> result = drivers.stream().map(d -> modelMapper.map(d, UserDto.class)).collect(Collectors.toList());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/drivers")
    public ResponseEntity<List<UserDto>> getAllDrivers() {
        List<User> users = userService.getAllUsers();
        List<UserDto> result = users.stream()
                .filter(u -> u.getRoles().equals("DRIVER"))
                .map(u -> modelMapper.map(u, UserDto.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/drivers/{id}")
    public ResponseEntity<UserDto> getDriverById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent() && user.get().getRoles().equals("DRIVER")) {
            return new ResponseEntity<>(modelMapper.map(user.get(), UserDto.class), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        boolean success = userService.changePassword(request.getEmail(), request.getOldPassword(), request.getNewPassword());
        if (success) {
            return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getMyDetails(java.security.Principal principal) {
        Optional<User> user = userService.findByEmail(principal.getName());
        return user.map(u -> new ResponseEntity<>(modelMapper.map(u, UserDto.class), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
