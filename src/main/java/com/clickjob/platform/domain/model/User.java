package com.clickjob.platform.domain.model;

import com.clickjob.platform.domain.enums.UserType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType;

    @Column(nullable = false)
    private String name;


    @Column(length = 20)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String aboutMe;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "employer", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Job> jobs = new ArrayList<>();

    @OneToMany(mappedBy = "freelancer", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Application> applications = new ArrayList<>();
}