package com.example.post.test.repository;
import com.example.post.test.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndPassword(String email, String password);
    List<User> findByRolesAndRegionId(String roles, Long regionId);
    List<User> findByRolesAndStatus(String roles, String status);
    Optional<User> findByEmail(String email);
    long countByRolesAndStatus(String roles, String status);
}
