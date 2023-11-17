package com.example.farmusfarm.domain.challenge.repository;

import com.example.farmusfarm.domain.challenge.entity.MissionPostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MissionPostLikeRepository extends JpaRepository<MissionPostLike, Long> {

    Optional<MissionPostLike> findByMissionPostIdAndUserId(Long missionPostId, Long userId);
}
