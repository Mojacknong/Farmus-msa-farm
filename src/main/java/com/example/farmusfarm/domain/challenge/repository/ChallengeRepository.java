package com.example.farmusfarm.domain.challenge.repository;

import com.example.farmusfarm.domain.challenge.dto.res.SearchChallengeResponseDto;
import com.example.farmusfarm.domain.challenge.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    @Query("select c from challenge c where c.difficulty in :difficulty and c.startedAt = :startedAtExists")
    List<Challenge> findAllByDifficultyIsInAndStartedAtExists(List<String> difficulty, boolean startedAtExists);

    @Query("select c from challenge c where c.difficulty in :difficulty")
    List<Challenge> findAllByDifficultyIsIn(List<String> difficulty);
}
