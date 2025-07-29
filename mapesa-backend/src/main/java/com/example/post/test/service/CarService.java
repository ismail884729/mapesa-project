package com.example.post.test.service;


import com.example.post.test.entity.Car;
import java.util.List;
import java.util.Optional;

public interface CarService {
    Car saveCar(Car car);
    List<Car> getAllCars();
    Optional<Car> getCarById(Long id);
    void deleteCar(Long id);
    boolean isExist(Long id);
}
