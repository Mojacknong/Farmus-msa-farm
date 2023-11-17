package com.example.farmusfarm.domain.veggie.repository;

import com.example.farmusfarm.domain.veggie.entity.DiaryLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiaryLikeRepository extends JpaRepository<DiaryLike, Long> {

    // 좋아요 여부 조회
    Optional<DiaryLike> findByDiaryIdAndUserId(Long diaryId, Long userId);
}
