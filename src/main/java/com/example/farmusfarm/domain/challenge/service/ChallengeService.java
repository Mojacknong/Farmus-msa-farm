package com.example.farmusfarm.domain.challenge.service;

import com.example.farmusfarm.domain.challenge.dto.req.CreateChallengeRequestDto;
import com.example.farmusfarm.domain.challenge.dto.res.CreateChallengeResponseDto;
import com.example.farmusfarm.domain.challenge.entity.Challenge;
import com.example.farmusfarm.domain.challenge.entity.Registration;
import com.example.farmusfarm.domain.challenge.repository.ChallengeRepository;
import com.example.farmusfarm.domain.challenge.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final RegistrationRepository registrationRepository;

    // 챌린지 생성
    public CreateChallengeResponseDto createChallenge(Long veggieId, CreateChallengeRequestDto requestDto) {

        // 챌린지 생성
        Challenge newChallenge = Challenge.createChallenge(requestDto.getVeggieInfoId(), requestDto.getVeggieName(), requestDto.getDifficulty(), requestDto.getImage(), requestDto.getChallengeName(), requestDto.getMaxUser(), requestDto.getDescription());
        Challenge savedChallenge = challengeRepository.save(newChallenge);

        return CreateChallengeResponseDto.of(savedChallenge.getId());
    }

    // 유저 별 전체 챌린지 조회
    public List<Registration> getRegistrationList(Long userId) {
        return registrationRepository.findAllByUserId(userId);
    }

    // 내 챌린지 목록 조회
    public List<Long> getMyChallengeList(Long userId) {
        return registrationRepository.findAllChallengeIdByUserId(userId);
    }

    // 챌린지 조회
    public Challenge getChallenge(Long challengeId) {
        return challengeRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("챌린지가 존재하지 않습니다."));
    }
}
