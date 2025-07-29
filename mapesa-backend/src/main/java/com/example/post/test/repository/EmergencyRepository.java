package com.example.post.test.repository;

import com.example.post.test.entity.Emergency;
import com.example.post.test.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmergencyRepository extends JpaRepository<Emergency, Long> {
    long countByStatus(Status status);

    List<Emergency> findByDriverId(Long driverId);

    List<Emergency> findByReporterId(Long reporterId);
}
