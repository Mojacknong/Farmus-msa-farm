package com.example.farmusfarm.domain.challenge.service;

import com.example.farmusfarm.common.S3Service;
import com.example.farmusfarm.common.Utils;
import com.example.farmusfarm.domain.challenge.dto.req.CreateMissionPostRequestDto;
import com.example.farmusfarm.domain.challenge.dto.res.CreateMissionPostResponseDto;
import com.example.farmusfarm.domain.challenge.dto.res.LikeMissionPostResponseDto;
import com.example.farmusfarm.domain.challenge.entity.*;
import com.example.farmusfarm.domain.challenge.repository.MissionPostImageRepository;
import com.example.farmusfarm.domain.challenge.repository.MissionPostLikeRepository;
import com.example.farmusfarm.domain.challenge.repository.MissionPostRepository;
import com.example.farmusfarm.domain.challenge.repository.RegistrationRepository;
import com.example.farmusfarm.domain.history.dto.req.CreateHistoryClubDetailRequestDto;
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

    private final MissionPostRepository missionPostRepository;
    private final MissionPostImageRepository missionPostImageRepository;
    private final MissionPostLikeRepository missionPostLikeRepository;
    private final RegistrationRepository registrationRepository;
    private final VeggieRepository veggieRepository;

    private final S3Service s3Service;

    private final CropFeignClient cropFeignClient;

    public CreateMissionPostResponseDto createMissionPost(Long userId, CreateMissionPostRequestDto requestDto, MultipartFile image) {
        Registration registration = registrationRepository.findById(requestDto.getRegistrationId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 등록입니다."));

        MissionPost newPost = MissionPost.createMissionPost(requestDto.getContent(), registration);
        MissionPost savedPost = missionPostRepository.save(newPost);

        // 이미지 업로드
        String imageUrl = s3Service.uploadImage(image, "missionPost");
        MissionPostImage missionPostImage = MissionPostImage.createMissionPostImage(imageUrl, savedPost);
        missionPostImageRepository.save(missionPostImage);

        if (registration.getCurrentStep() == registration.getChallenge().getMaxStep() - 1) {
            completeChallenge(registration, userId);
        } else {
            // 다음 스텝 불러오기
            String nextStep = cropFeignClient.getVeggieInfoStepName(registration.getVeggie().getVeggieInfoId(), savedPost.getStep() + 1).getStepName();
            registration.updateCurrentStep(nextStep);
            registrationRepository.save(registration);

        }

        return CreateMissionPostResponseDto.of(savedPost.getId(), registration.getChallenge().getChallengeName(), savedPost.getStep(), imageUrl);
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

    public void completeChallenge(Registration registration, Long userId) {
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
//        CreateHistoryClubDetailRequestDto requestDto = new CreateHistoryClubDetailRequestDto(
//                challenge.getImageUrl(),
//                challenge.getVeggieName(),
//                challenge.getChallengeName(),
//
//
//        )
    }
}
