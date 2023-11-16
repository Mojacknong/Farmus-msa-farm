package com.example.farmusfarm.domain.challenge.service;

import com.example.farmusfarm.common.S3Service;
import com.example.farmusfarm.domain.challenge.dto.req.CreateMissionPostRequestDto;
import com.example.farmusfarm.domain.challenge.dto.res.CreateMissionPostResponseDto;
import com.example.farmusfarm.domain.challenge.entity.MissionPost;
import com.example.farmusfarm.domain.challenge.entity.MissionPostImage;
import com.example.farmusfarm.domain.challenge.entity.Registration;
import com.example.farmusfarm.domain.challenge.repository.MissionPostImageRepository;
import com.example.farmusfarm.domain.challenge.repository.MissionPostLikeRepository;
import com.example.farmusfarm.domain.challenge.repository.MissionPostRepository;
import com.example.farmusfarm.domain.challenge.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class MissionPostService {

    private final MissionPostRepository missionPostRepository;
    private final MissionPostImageRepository missionPostImageRepository;
    private final MissionPostLikeRepository missionPostLikeRepository;
    private final RegistrationRepository registrationRepository;

    private final S3Service s3Service;
    private final ChallengeService challengeService;

    public CreateMissionPostResponseDto createMissionPost(CreateMissionPostRequestDto requestDto, MultipartFile image) {
        Registration registration = registrationRepository.findById(requestDto.getRegistrationId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 등록입니다."));

        MissionPost newPost = MissionPost.createMissionPost(requestDto.getContent(), registration);
        MissionPost savedPost = missionPostRepository.save(newPost);

        // 이미지 업로드
        String imageUrl = s3Service.uploadImage(image, "missionPost");
        MissionPostImage missionPostImage = MissionPostImage.createMissionPostImage(imageUrl, savedPost);
        missionPostImageRepository.save(missionPostImage);

        // 다음 스텝 불러오기
        String nextStep = "다음 스텝";
        registration.updateCurrentStep(nextStep);
        registrationRepository.save(registration);

        return CreateMissionPostResponseDto.of(savedPost.getId(), registration.getChallenge().getChallengeName(), savedPost.getStep(), imageUrl);
    }
}
