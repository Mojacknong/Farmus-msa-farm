package com.example.farmusfarm.domain.challenge.repository;

import com.example.farmusfarm.domain.challenge.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
}
