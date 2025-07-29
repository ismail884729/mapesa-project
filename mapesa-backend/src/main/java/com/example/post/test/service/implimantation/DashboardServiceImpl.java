package com.example.post.test.service.implimantation;

import com.example.post.test.DTOs.DashboardStatsDto;
import com.example.post.test.enums.Status;
import com.example.post.test.repository.CarRepository;
import com.example.post.test.repository.EmergencyRepository;
import com.example.post.test.repository.UserRepository;
import com.example.post.test.service.DashboardService;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final EmergencyRepository emergencyRepository;

    public DashboardServiceImpl(UserRepository userRepository, CarRepository carRepository, EmergencyRepository emergencyRepository) {
        this.userRepository = userRepository;
        this.carRepository = carRepository;
        this.emergencyRepository = emergencyRepository;
    }

    @Override
    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();

        // Total Users
        stats.setTotalUsers(userRepository.count());

        // Active Drivers (assuming "ACTIVE" status for users with "DRIVER" role)
        long activeDriversCount = userRepository.countByRolesAndStatus("DRIVER", "ACTIVE");
        stats.setActiveDrivers(activeDriversCount);


        // Available Cars (counting all cars for now, as no specific "available" status on Car entity)
        stats.setAvailableCars(carRepository.count());

        // Pending Reports
        stats.setPendingReports(emergencyRepository.countByStatus(Status.PENDING));

        return stats;
    }
}
