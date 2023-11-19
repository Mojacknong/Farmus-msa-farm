package com.example.farmusfarm.domain.challenge.service;

import com.example.farmusfarm.common.S3Service;
import com.example.farmusfarm.common.Utils;
import com.example.farmusfarm.domain.challenge.dto.req.CreateMissionPostRequestDto;
import com.example.farmusfarm.domain.challenge.dto.res.CompleteChallengeResponseDto;
import com.example.farmusfarm.domain.challenge.dto.res.CreateMissionPostResponseDto;
import com.example.farmusfarm.domain.challenge.dto.res.LikeMissionPostResponseDto;
import com.example.farmusfarm.domain.challenge.entity.*;
import com.example.farmusfarm.domain.challenge.repository.*;
import com.example.farmusfarm.domain.history.dto.req.CreateHistoryClubDetailRequestDto;
import com.example.farmusfarm.domain.veggie.repository.DiaryRepository;
import com.example.farmusfarm.domain.veggie.repository.VeggieRepository;
import com.example.farmusfarm.domain.veggieInfo.openfeign.CropFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
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

    public CreateMissionPostResponseDto createMissionPost(CreateMissionPostRequestDto requestDto, MultipartFile image) {
        Registration registration = registrationRepository.findById(requestDto.getRegistrationId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 등록입니다."));

        MissionPost newPost = MissionPost.createMissionPost(requestDto.getContent(), registration);
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

        return CreateMissionPostResponseDto.of(registration.getId(), registration.getChallenge().getChallengeName(), savedPost.getStep(), imageUrl, isEnd);
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
}
