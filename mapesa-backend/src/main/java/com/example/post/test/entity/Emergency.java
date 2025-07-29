package com.example.post.test.entity;

import com.example.post.test.enums.Status;
import jakarta.persistence.*;
import lombok.Data;
// import com.example.post.test.entity.Driver; // Ensure this import is present and correct

import java.time.LocalDateTime;

@Entity
@Table(name = "emergencies")
@Data
public class Emergency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    // Location coordinates
    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    private String locationDescription;

    @Column(nullable = false)
    private LocalDateTime reportedAt;

    private LocalDateTime respondedAt;
    private LocalDateTime completedAt;

    @ManyToOne
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne
    @JoinColumn(name = "dispatcher_id")
    private User dispatcher;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private User driver; // Changed from Driver to User

    @PrePersist
    protected void onCreate() {
        this.reportedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = Status.PENDING;
        }
    }
}
