package com.example.farmusfarm.domain.challenge.repository;

import com.example.farmusfarm.domain.challenge.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    List<Registration> findAllByUserId(Long userId);

    List<Long> findAllChallengeIdByUserId(Long userId);
}
