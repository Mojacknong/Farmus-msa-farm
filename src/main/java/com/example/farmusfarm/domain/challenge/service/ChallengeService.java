package com.example.farmusfarm.domain.challenge.service;

import com.example.farmusfarm.common.Utils;
import com.example.farmusfarm.domain.challenge.dto.req.CreateChallengeRequestDto;
import com.example.farmusfarm.domain.challenge.dto.req.FinishChallengeRequestDto;
import com.example.farmusfarm.domain.challenge.dto.res.*;
import com.example.farmusfarm.domain.challenge.entity.Challenge;
import com.example.farmusfarm.domain.challenge.entity.MissionPost;
import com.example.farmusfarm.domain.challenge.entity.Registration;
import com.example.farmusfarm.domain.challenge.repository.ChallengeRepository;
import com.example.farmusfarm.domain.challenge.repository.RegistrationRepository;
import com.example.farmusfarm.domain.user.openfeign.UserFeignClient;
import com.example.farmusfarm.domain.veggie.dto.res.GetDiaryResponseDto;
import com.example.farmusfarm.domain.veggie.entity.Diary;
import com.example.farmusfarm.domain.veggie.entity.Veggie;
import com.example.farmusfarm.domain.veggie.repository.DiaryRepository;
import com.example.farmusfarm.domain.veggie.repository.VeggieRepository;
import com.example.farmusfarm.domain.veggieInfo.dto.res.GetStepNameResponseDto;
import com.example.farmusfarm.domain.veggieInfo.dto.res.VeggieInfoResponseDto;
import com.example.farmusfarm.domain.veggieInfo.openfeign.CropFeignClient;
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
    private final VeggieRepository veggieRepository;

    private final CropFeignClient cropFeignClient;
    private final UserFeignClient userFeignClient;

    // 챌린지 생성
    public CreateChallengeResponseDto createChallenge(Long userId, CreateChallengeRequestDto requestDto) {

        // 채소 정보 가져오기
        VeggieInfoResponseDto veggieInfo = cropFeignClient.getVeggieInfo(requestDto.getVeggieInfoId());

        // 챌린지 생성
        Challenge newChallenge = Challenge.createChallenge(
                requestDto.getVeggieInfoId(), veggieInfo.getVeggieName(), veggieInfo.getDifficulty(), veggieInfo.getImageUrl(),
                veggieInfo.getGrayImageUrl(), requestDto.getChallengeName(), veggieInfo.getStepNum(), requestDto.getMaxUser(), requestDto.getDescription());
        Challenge savedChallenge = challengeRepository.save(newChallenge);

        createRegistration(userId, requestDto.getMyVeggieId(), savedChallenge.getId(), veggieInfo.getFirstStepName());

        return CreateChallengeResponseDto.of(savedChallenge.getId());
    }

    // 챌린지 참여
    public CreateRegistrationResponseDto createRegistration(Long userId, Long veggieId, Long challengeId, String firstMission) {
        Challenge challenge = getChallenge(challengeId);
        Veggie veggie = getVeggie(veggieId);

        validateRegistration(veggieId, challengeId);

        Registration registration = Registration.createRegistration(userId, firstMission, challenge, veggie);
        Registration savedRegistration = registrationRepository.save(registration);

        return CreateRegistrationResponseDto.of(savedRegistration.getId());
    }

    // 챌린지 신규 참여
    public CreateRegistrationResponseDto createRegistration(Long userId, Long veggieId, Long challengeId) {
        Challenge challenge = getChallenge(challengeId);
        Veggie veggie = getVeggie(veggieId);

        validateRegistration(veggieId, challengeId);
        // 첫 미션 명 불러오기
        String mission = cropFeignClient.getVeggieInfoStepName(veggie.getVeggieInfoId(), 0).getStepName();

        Registration registration = Registration.createRegistration(userId, mission, challenge, veggie);
        Registration savedRegistration = registrationRepository.save(registration);

        return CreateRegistrationResponseDto.of(savedRegistration.getId());
    }

    public FinishChallengeResponseDto finishChallenge(Long userId, FinishChallengeRequestDto requestDto) {
        Registration registration = getUserRegistration(userId, requestDto.getChallengeId());

        // 채소를 계속 재배할 경우 등록 정보만 삭제
        if (requestDto.getIsContinue()) {
            registrationRepository.delete(registration);
        } else {
            // 채소를 재배하지 않을 경우 챌린지 종료
            veggieRepository.delete(registration.getVeggie());
        }

        return FinishChallengeResponseDto.of(true);
    }

    public GetChallengeInfoResponse getChallengeDetail(Long challengeId, Long userId) {

        if (checkAlreadyRegistered(userId, challengeId)) {
            return getMyChallengeInfo(userId, challengeId);
        } else {
            return getOtherChallengeInfo(challengeId);
        }

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

    // 전체 챌린지 목록 검색
    public List<SearchChallengeResponseDto> searchChallengeList(Long userId, List<String> difficulties, String status, String keyword) {
        List<Challenge> challengeList;

        if (difficulties.isEmpty()) {
            difficulties = List.of("Easy", "Normal", "Hard");
        }

        if (status.equals("All")) {
            challengeList = challengeRepository.findAllByDifficultyIsIn(difficulties);
        } else if (status.equals("준비 중")) {
            challengeList = challengeRepository.findAllByDifficultyIsInAndStartedAtIsNull(difficulties);
        } else {
            challengeList = challengeRepository.findAllByDifficultyIsInAndStartedAtIsNotNull(difficulties);
        }

        if (!keyword.isEmpty()) {
            challengeList = searchByKeyword(challengeList, keyword);
        }

        return streamChallengeListToSearchResult(challengeList, userId);
    }

    public List<Challenge> searchByKeyword(List<Challenge> challengeList, String keyword) {
        // find which contains keyword in veggieName or challengeName
        return challengeList.stream()
                .filter(c -> c.getVeggieName().contains(keyword) || c.getChallengeName().contains(keyword))
                .collect(Collectors.toList());
    }

    // 추천 챌린지 조회
    public List<SearchChallengeResponseDto> getRecommendedChallengeList(Long userId) {
        // 유저 별 추천 난이도 조회
        String difficulty = "Hard";

        return getChallengeListByDifficulty(userId, difficulty);
    }

    // 난이도 별 챌린지 조회
    public List<SearchChallengeResponseDto> getChallengeListByDifficulty(Long userId, String difficulty) {
        List<Challenge> challengeList = challengeRepository.findAllByDifficultyIs(difficulty);

        return streamChallengeListToSearchResult(challengeList, userId);
    }

    private String getStatusMessage(LocalDate statedAt) {
        return statedAt == null ? "준비 중" : "시작한 지 " + Utils.compareLocalDate(LocalDate.now(), statedAt) + "일째";
    }

    private int getStatusCount(LocalDate statedAt) {
        return statedAt == null ? -1 : Utils.compareLocalDate(LocalDate.now(), statedAt);
    }

    private List<SearchChallengeResponseDto> streamChallengeListToSearchResult(List<Challenge> challengeList, Long userId) {
        return challengeList.stream()
                .filter(c -> !checkAlreadyRegistered(userId, c.getId()))
                .map(c -> {
            String cStatus = getStatusMessage(c.getStartedAt());

            return SearchChallengeResponseDto.of(
                    c.getId(),
                    c.getVeggieName(),
                    c.getChallengeName(),
                    c.getImageUrl(),
                    c.getDifficulty(),
                    c.getMaxUser(),
                    c.getRegistrations().size(),
                    cStatus
            );
        }).collect(Collectors.toList());
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
    public List<Diary> getAllDiaryEntitiesByChallenge(Long challengeId) {
        return diaryRepository.findAllByChallengeId(challengeId);
    }

    // 챌린지 일기 전체 조회
    public List<GetDiaryResponseDto> getDiaryListByChallenge(Long challengeId) {
        List<Diary> diaryList = getAllDiaryEntitiesByChallenge(challengeId);

        // 유저 정보를 가져옴 (이미지, 닉네임)

        return diaryList.stream().map(d -> GetDiaryResponseDto.of(
                "", "", d.getDiaryImages().get(0).getImageUrl(), d.getContent(), Utils.dateTimeFormat(d.getCreatedDate()))
        ).collect(Collectors.toList());
    }

    // 그룹 달성률 조회
    public List<Integer> getChallengeAchievement(Long challengeId, int maxStep) {
        List<Registration> registrationList = getAllRegistrationByChallenge(challengeId);

        // 모든 값이 0으로 초기화되고, 크기는 maxStep인 ArrayList 생성
        List<Integer> achievement = new ArrayList<>(maxStep);
        for (int i = 0; i < maxStep; i++) {
            achievement.add(0);
        }

        log.info(achievement.toString());

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
        List<String> imageList = new ArrayList<String>();
        challenge.getRegistrations().forEach(r -> {
                    for (MissionPost p : r.getMissionPosts()) {
                        if (p.getStep() == registration.getCurrentStep()) {
                            imageList.add(p.getMissionPostImages().get(0).getImageUrl());
                        }
                    }
                });

        // veggieInfoId로 스텝이랑 도움말 정보 가져오기

        return GetChallengeInfoResponse.of(
                challenge.getChallengeName(),
                challenge.getVeggieName(),
                challenge.getDescription(),
                challenge.getImageUrl(),
                challenge.getDifficulty(),
                challenge.getMaxUser(),
                challenge.getRegistrations().size(),
                getStatusCount(challenge.getStartedAt()),
                getChallengeAchievement(challengeId, challenge.getMaxStep()),
                registration.getCurrentStep(),
                registration.getCurrentStepName(),
                "",
                imageList,
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
                challenge.getImageUrl(),
                challenge.getDifficulty(),
                challenge.getMaxUser(),
                challenge.getRegistrations().size(),
                getStatusCount(challenge.getStartedAt()),
                null,
                0,
                "준비물을 챙겨요",
                "",
                new ArrayList<String>(),
                null
        );
    }

    // 채소 조회
    public Veggie getVeggie(Long veggieId) {
        return veggieRepository.findById(veggieId)
                .orElseThrow(() -> new IllegalArgumentException("채소가 존재하지 않습니다."));
    }

    public void validateRegistration(Long userId, Long challengeId) {
        // 챌린지 참여 인원수 체크
        if (registrationRepository.findAllByChallengeId(challengeId).size() >= getChallenge(challengeId).getMaxUser()) {
            throw new IllegalArgumentException("챌린지 인원이 초과되었습니다.");
        }

        // 이미 참여한 챌린지인지 체크
        if (checkAlreadyRegistered(userId, challengeId)) {
            throw new IllegalArgumentException("이미 참여한 챌린지입니다.");
        }
    }

    public Boolean checkAlreadyRegistered(Long userId, Long challengeId) {
        return registrationRepository.findByUserIdAndChallengeId(userId, challengeId).isPresent();
    }

    public GetStepNameResponseDto test() {
        log.info("test");
        return cropFeignClient.getVeggieInfoStepName("65550000f986782347487451", 0);
    }
}
