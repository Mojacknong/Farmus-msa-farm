package com.example.farmusfarm.domain.challenge.repository;

import com.example.farmusfarm.domain.challenge.dto.res.GetMyChallengeListDto;
import com.example.farmusfarm.domain.challenge.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    List<Registration> findAllByUserId(Long userId);

    // 챌린지 id랑 챌린지 image의 리스트 조회
    @Query("select new com.example.farmusfarm.domain.challenge.dto.res.GetMyChallengeListDto(r.challenge.id, r.challenge.imageUrl, r.challenge.grayImage) from registration r where r.userId = :userId")
    List<GetMyChallengeListDto> findAllChallengeIdAndImageByUserId(Long userId);

    List<Registration> findAllByChallengeId(Long challengeId);

    Optional<Registration> findByUserIdAndChallengeId(Long userId, Long challengeId);

    Optional<Registration> findByVeggieIdAndChallengeId(Long veggieId, Long challengeId);

    // 챌린지에 현재 스텝이 1 이상인 Registration의 개수 조회
    int countByChallengeIdAndCurrentStepGreaterThan(Long challengeId, int currentStep);
}
