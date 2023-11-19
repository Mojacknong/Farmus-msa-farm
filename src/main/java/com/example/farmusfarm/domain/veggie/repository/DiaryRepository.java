package com.example.farmusfarm.domain.veggie.repository;

import com.example.farmusfarm.domain.veggie.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    // 채소 별 일기 조회
    List<Diary> findAllByVeggieId(Long veggieId);

    // 챌린지 아이디로 전체 일기 조회
    List<Diary> findAllByChallengeId(Long challengeId);

    // 챌린지 아이디와 채소 아이디로 일기 조회
    List<Diary> findAllByChallengeIdAndVeggieId(Long challengeId, Long veggieId);
}
