package com.example.farmusfarm.domain.challenge.service;

import com.example.farmusfarm.common.Utils;
import com.example.farmusfarm.domain.challenge.dto.req.CreateChallengeRequestDto;
import com.example.farmusfarm.domain.challenge.dto.res.CreateChallengeResponseDto;
import com.example.farmusfarm.domain.challenge.dto.res.GetChallengeInfoResponse;
import com.example.farmusfarm.domain.challenge.dto.res.GetMyChallengeListDto;
import com.example.farmusfarm.domain.challenge.entity.Challenge;
import com.example.farmusfarm.domain.challenge.entity.Registration;
import com.example.farmusfarm.domain.challenge.repository.ChallengeRepository;
import com.example.farmusfarm.domain.challenge.repository.RegistrationRepository;
import com.example.farmusfarm.domain.veggie.dto.res.GetDiaryResponseDto;
import com.example.farmusfarm.domain.veggie.entity.Diary;
import com.example.farmusfarm.domain.veggie.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final RegistrationRepository registrationRepository;
    private final DiaryRepository diaryRepository;

    // 챌린지 생성
    public CreateChallengeResponseDto createChallenge(Long veggieId, CreateChallengeRequestDto requestDto) {

        // 챌린지 생성
        Challenge newChallenge = Challenge.createChallenge(requestDto.getVeggieInfoId(), requestDto.getVeggieName(), requestDto.getDifficulty(), requestDto.getImage(), requestDto.getChallengeName(), requestDto.getMaxStep(), requestDto.getMaxUser(), requestDto.getDescription());
        Challenge savedChallenge = challengeRepository.save(newChallenge);

        return CreateChallengeResponseDto.of(savedChallenge.getId());
    }

    // 유저 별 전체 챌린지 조회
    public List<Registration> getUserRegistrationList(Long userId) {
        return registrationRepository.findAllByUserId(userId);
    }

    // 유저 별 등록 정보 조회
    public Registration getUserRegistration(Long userId, Long challengeId) {
        return registrationRepository.findByUserIdAndChallengeId(userId, challengeId)
                .orElseThrow(() -> new IllegalArgumentException("등록 정보가 존재하지 않습니다."));
    }

    // 내 챌린지 목록 조회
    public List<GetMyChallengeListDto> getMyChallengeList(Long userId) {
        return registrationRepository.findAllChallengeIdAndImageByUserId(userId);
    }

    // 챌린지 조회
    public Challenge getChallenge(Long challengeId) {
        return challengeRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("챌린지가 존재하지 않습니다."));
    }

    // 챌린지 별 참여자 조회
    public List<Registration> getAllRegistrationByChallenge(Long challengeId) {
        return registrationRepository.findAllByChallengeId(challengeId);
    }

    // 챌린지 별 일기 객체 조회
    public List<Diary> getAllDiaryListByChallenge(Long challengeId) {
        return diaryRepository.findAllByChallengeId(challengeId);
    }

    // 챌린지 일기 전체 조회
    public List<GetDiaryResponseDto> getDiaryListByChallenge(Long challengeId) {
        List<Diary> diaryList = getAllDiaryListByChallenge(challengeId);

        // 유저 정보를 가져옴 (이미지, 닉네임)

        return diaryList.stream().map(d -> GetDiaryResponseDto.of(
                "", "", d.getDiaryImages().get(0).getImageUrl(), d.getContent(), d.getCreatedDate())
        ).collect(Collectors.toList());
    }

    // 그룹 달성률 조회
    public List<Integer> getChallengeAchievement(Long challengeId, int maxStep) {
        List<Registration> registrationList = getAllRegistrationByChallenge(challengeId);

        List<Integer> achievement = new ArrayList<>(maxStep + 1);

        // 등록 목록을 돌면서 각 스텝 별 인원수 체크
        for (Registration r : registrationList) {
            int step = r.getCurrentStep();
            achievement.set(step, achievement.get(step) + 1);
        }

        return achievement;
    }

    // 내 챌린지 정보 조회
    public GetChallengeInfoResponse getMyChallengeInfo(Long userId, Long challengeId) {
        Challenge challenge = getChallenge(challengeId);
        Registration registration = getUserRegistration(userId, challengeId);

        // veggieInfoId로 스텝이랑 도움말 정보 가져오기

        return GetChallengeInfoResponse.of(
                challenge.getChallengeName(),
                challenge.getVeggieName(),
                challenge.getDescription(),
                challenge.getImage(),
                challenge.getDifficulty(),
                challenge.getMaxUser(),
                challenge.getRegistrations().size(),
                Utils.compareLocalDate(LocalDate.parse(challenge.getStartedAt()), LocalDate.now()),
                getChallengeAchievement(challengeId, challenge.getMaxStep()),
                "",
                "",
                new ArrayList<String>(),
                getDiaryListByChallenge(challengeId)

        );
    }

    // 미가입 챌린지 정보 조회
    public GetChallengeInfoResponse getOtherChallengeInfo(Long challengeId) {
        Challenge challenge = getChallenge(challengeId);

        // veggieInfoId로 스텝이랑 도움말 정보 가져오기

        return GetChallengeInfoResponse.of(
                challenge.getChallengeName(),
                challenge.getVeggieName(),
                challenge.getDescription(),
                challenge.getImage(),
                challenge.getDifficulty(),
                challenge.getMaxUser(),
                challenge.getRegistrations().size(),
                Utils.compareLocalDate(LocalDate.parse(challenge.getStartedAt()), LocalDate.now()),
                null,
                "",
                "",
                new ArrayList<String>(),
                null
        );
    }
}
