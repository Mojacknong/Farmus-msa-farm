package com.example.farmusfarm.domain.challenge.repository;

import com.example.farmusfarm.domain.challenge.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
}
