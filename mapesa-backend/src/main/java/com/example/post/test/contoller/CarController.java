package com.example.post.test.contoller;

import com.example.post.test.DTOs.CarDto;
import com.example.post.test.entity.Car;
import com.example.post.test.service.CarService;
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
@RequestMapping("/car")
@Tag(name = "Car Management", description = "APIs for managing cars")
public class CarController {

    @Autowired
    private CarService carService;

    @Autowired
    private ModelMapper modelMapper;

    public CarController(CarService carService, ModelMapper modelMapper) {
        this.carService = carService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<CarDto> createCar(@RequestBody CarDto dto) {
        dto.setId(null); // 💡 force creation
        Car car = modelMapper.map(dto, Car.class);
        Car saved = carService.saveCar(car);
        return new ResponseEntity<>(modelMapper.map(saved, CarDto.class), HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<List<CarDto>> getAllCars() {
        List<Car> cars = carService.getAllCars();
        List<CarDto> result = cars.stream().map(c -> modelMapper.map(c, CarDto.class)).collect(Collectors.toList());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarDto> getCarById(@PathVariable Long id) {
        Optional<Car> car = carService.getCarById(id);
        return car.map(c -> new ResponseEntity<>(modelMapper.map(c, CarDto.class), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarDto> updateCar(@PathVariable Long id, @RequestBody CarDto dto) {
        if (!carService.isExist(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        dto.setId(id);
        Car updated = modelMapper.map(dto, Car.class);
        Car saved = carService.saveCar(updated);
        return new ResponseEntity<>(modelMapper.map(saved, CarDto.class), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
