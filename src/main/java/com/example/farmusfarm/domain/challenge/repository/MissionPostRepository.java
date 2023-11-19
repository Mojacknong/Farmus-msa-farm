package com.example.farmusfarm.domain.challenge.repository;

import com.example.farmusfarm.domain.challenge.entity.MissionPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MissionPostRepository extends JpaRepository<MissionPost, Long> {

    // 챌린지에 해당하는 모든 등록정보를 통해 모든 미션 포스트를 가져온다.
    // 특정 step 에 해당하는 미션 포스트를 가져온다.
    @Query("select m from mission_post m where m.registration.challenge.id = :challengeId and m.step = :step")
    List<MissionPost> findAllByChallengeIdAndStep(Long challengeId, int step);
}
