package com.example.farmusfarm.domain.challenge.repository;

import com.example.farmusfarm.domain.challenge.dto.res.SearchChallengeResponseDto;
import com.example.farmusfarm.domain.challenge.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    List<Challenge> findAllByDifficultyIsInAndStartedAtIsNotNull(List<String> difficulty);
    List<Challenge> findAllByDifficultyIsInAndStartedAtIsNull(List<String> difficulty);

    @Query("select c from challenge c where c.difficulty in :difficulty")
    List<Challenge> findAllByDifficultyIsIn(List<String> difficulty);

    List<Challenge> findAllByDifficultyIs(String difficulty);
}
