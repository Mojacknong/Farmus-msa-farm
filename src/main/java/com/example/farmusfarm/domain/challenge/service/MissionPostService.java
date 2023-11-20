package com.example.farmusfarm.domain.challenge.service;

import com.example.farmusfarm.common.S3Service;
import com.example.farmusfarm.common.Utils;
import com.example.farmusfarm.domain.challenge.dto.req.CreateMissionPostRequestDto;
import com.example.farmusfarm.domain.challenge.dto.res.CompleteChallengeResponseDto;
import com.example.farmusfarm.domain.challenge.dto.res.CreateMissionPostResponseDto;
import com.example.farmusfarm.domain.challenge.dto.res.GetFarmClubPostResponseDto;
import com.example.farmusfarm.domain.challenge.dto.res.LikeMissionPostResponseDto;
import com.example.farmusfarm.domain.challenge.entity.*;
import com.example.farmusfarm.domain.challenge.repository.*;
import com.example.farmusfarm.domain.user.dto.res.UserInfoDto;
import com.example.farmusfarm.domain.user.openfeign.UserFeignClient;
import com.example.farmusfarm.domain.veggie.entity.Diary;
import com.example.farmusfarm.domain.veggieInfo.dto.req.CreateHistoryClubDetailRequestDto;
import com.example.farmusfarm.domain.veggie.repository.DiaryRepository;
import com.example.farmusfarm.domain.veggieInfo.openfeign.CropFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MissionPostService {
    private final ChallengeRepository challengeRepository;

    private final MissionPostRepository missionPostRepository;
    private final MissionPostImageRepository missionPostImageRepository;
    private final MissionPostLikeRepository missionPostLikeRepository;
    private final RegistrationRepository registrationRepository;
    private final DiaryRepository diaryRepository;

    private final S3Service s3Service;

    private final CropFeignClient cropFeignClient;
    private final UserFeignClient userFeignClient;

    public CreateMissionPostResponseDto createMissionPost(Long registrationId, String content, MultipartFile image) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 등록입니다."));

        MissionPost newPost = MissionPost.createMissionPost(content, registration);
        MissionPost savedPost = missionPostRepository.save(newPost);

        // 이미지 업로드
        String imageUrl = s3Service.uploadImage(image, "missionPost");
        MissionPostImage missionPostImage = MissionPostImage.createMissionPostImage(imageUrl, savedPost);
        missionPostImageRepository.save(missionPostImage);

        Challenge challenge = registration.getChallenge();
        boolean isEnd = false;

        if (registration.getCurrentStep() == challenge.getMaxStep() - 1) {
            isEnd = true;
        } else {
            // 다음 스텝 불러오기
            String nextStep = cropFeignClient.getVeggieInfoStepName(registration.getVeggie().getVeggieInfoId(), savedPost.getStep() + 1).getStepName();
            registration.updateCurrentStep(nextStep);
            registrationRepository.save(registration);

            // 챌린지에 현재 스텝이 1 이상인 Registration이 3개 이상이라면 챌린지 시작일 설정
            if (savedPost.getStep() == 0 && registrationRepository.countByChallengeIdAndCurrentStepGreaterThan(challenge.getId(), 0) == 3 ) {
                challenge.setStartedAt(LocalDate.now());
                challengeRepository.save(challenge);
            }
        }

        return CreateMissionPostResponseDto.of(registration.getId(), challenge.getChallengeName(), savedPost.getStep(), challenge.getImageUrl(), isEnd);
    }

    public LikeMissionPostResponseDto likeMissionPost(Long userId, Long missionPostId) {
        MissionPost missionPost = getMissionPost(missionPostId);
        Optional<MissionPostLike> like = missionPostLikeRepository.findByMissionPostIdAndUserId(missionPostId, userId);
        Boolean result = like.isEmpty();

        if (result) {
            missionPostLikeRepository.save(MissionPostLike.createMissionPostLike(missionPost, userId));
        } else {
            missionPostLikeRepository.delete(like.get());
        }

        return LikeMissionPostResponseDto.of(result);
    }

    public MissionPost getMissionPost(Long missionPostId) {
        return missionPostRepository.findById(missionPostId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 미션 포스트입니다."));
    }

    public CompleteChallengeResponseDto completeChallenge(Long registrationId, Long userId) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 등록입니다."));

        // 등록 정보에서 미션 포스트 리스트 돌면서 HistoryClubPost에 저장
        List<CreateHistoryClubDetailRequestDto.HistoryClubPost> missionPosts = registration.getMissionPosts()
                .stream()
                .map(missionPost -> CreateHistoryClubDetailRequestDto.HistoryClubPost.builder()
                        .stepNum(missionPost.getStep())
                        .stepName(registration.getCurrentStepName())
                        .postImage(missionPost.getMissionPostImages().get(0).getImageUrl())
                        .content(missionPost.getContent())
                        .dateTime(Utils.dateTimeFormat(missionPost.getCreatedDate()))
                        .likeNum(missionPost.getMissionPostLikes().size())
                        .build())
                .collect(Collectors.toList());

        Challenge challenge = registration.getChallenge();
        // 히스토리 생성
        CreateHistoryClubDetailRequestDto requestDto = new CreateHistoryClubDetailRequestDto(
                challenge.getImageUrl(),
                challenge.getVeggieName(),
                challenge.getChallengeName(),
                challenge.getStartedAt() + "~" + LocalDate.now(),
                missionPosts
        );

        Long veggieId = registration.getVeggie().getId();

        cropFeignClient.createHistoryClubDetail(userId, requestDto);
        registrationRepository.delete(registration);

        int day = Utils.compareLocalDate(LocalDate.now(), challenge.getStartedAt());
        int mission = challenge.getMaxStep();
        int diary = diaryRepository.findAllByChallengeIdAndVeggieId(challenge.getId(), registration.getVeggie().getId()).size();

        return CompleteChallengeResponseDto.of(veggieId , challenge.getImageUrl(), challenge.getChallengeName(), day, mission, diary);
    }

    public List<GetFarmClubPostResponseDto> getMissionPosts(Long challengeId, int stepNum) {

        List<UserInfoDto> users = userFeignClient.getAllUser().getData().getAllUserDtoList();
        log.info("users: {}", users);

        // users를 통해 id : nickname 해시맵 만들기
        HashMap<Long, String> nicknameMap = new HashMap<>();
        for (UserInfoDto user : users) {
            nicknameMap.put(user.getId(), user.getNickName());
        }
        HashMap<Long, String> imageMap = new HashMap<>();
        for (UserInfoDto user : users) {
            imageMap.put(user.getId(), user.getImageUrl());
        }

        List<MissionPost> missionPosts = missionPostRepository.findAllByChallengeIdAndStep(challengeId, stepNum);
        return streamMissionPosts(missionPosts, nicknameMap, imageMap);
    }

    public List<GetFarmClubPostResponseDto> streamMissionPosts(List<MissionPost> missionPosts, HashMap<Long, String> nicknameMap, HashMap<Long, String> imageMap) {
        return missionPosts.stream()
                .map(missionPost -> GetFarmClubPostResponseDto.of(
                        missionPost.getId(),
                        imageMap.get(missionPost.getRegistration().getUserId()),
                        nicknameMap.get(missionPost.getRegistration().getUserId()),
                        Utils.dateTimeFormat(missionPost.getCreatedDate()),
                        missionPost.getMissionPostImages().get(0).getImageUrl(),
                        missionPost.getContent(),
                        missionPost.getMissionPostLikes().size()
                ))
                .collect(Collectors.toList());
    }

    public List<GetFarmClubPostResponseDto> getDiaryPosts(Long challengeId) {

        List<UserInfoDto> users = userFeignClient.getAllUser().getData().getAllUserDtoList();
        log.info("users: {}", users);

        // users를 통해 id : nickname 해시맵 만들기
        HashMap<Long, String> nicknameMap = new HashMap<>();
        for (UserInfoDto user : users) {
            nicknameMap.put(user.getId(), user.getNickName());
        }
        HashMap<Long, String> imageMap = new HashMap<>();
        for (UserInfoDto user : users) {
            imageMap.put(user.getId(), user.getImageUrl());
        }

        List<Diary> diaryPosts = diaryRepository.findAllByChallengeId(challengeId);
        return streamDiaryPosts(diaryPosts, nicknameMap, imageMap);
    }

    public List<GetFarmClubPostResponseDto> streamDiaryPosts(List<Diary> diaryPosts, HashMap<Long, String> nicknameMap, HashMap<Long, String> imageMap) {
        return diaryPosts.stream()
                .map(diaryPost -> GetFarmClubPostResponseDto.of(
                        diaryPost.getId(),
                        imageMap.get(diaryPost.getVeggie().getUserId()),
                        nicknameMap.get(diaryPost.getVeggie().getUserId()),
                        Utils.dateTimeFormat(diaryPost.getCreatedDate()),
                        diaryPost.getDiaryImages().get(0).getImageUrl(),
                        diaryPost.getContent(),
                        diaryPost.getDiaryLikes().size()
                ))
                .collect(Collectors.toList());
    }
}
