package com.example.farmusfarm.domain.challenge.controller;

import com.example.farmusfarm.common.JwtTokenProvider;
import com.example.farmusfarm.common.S3Service;
import com.example.farmusfarm.domain.challenge.dto.req.*;
import com.example.farmusfarm.domain.challenge.service.ChallengeService;
import com.example.farmusfarm.domain.challenge.service.MissionPostService;
import com.example.farmusfarm.domain.veggie.service.DiaryService;
import com.example.farmusfarm.domain.veggieInfo.dto.res.GetStepNameResponseDto;
import com.example.farmusfarm.global.response.BaseResponseDto;
import com.example.farmusfarm.global.response.SuccessMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/farmclub")
public class ChallengeController {

    private final ChallengeService challengeService;
    private final MissionPostService missionPostService;
    private final DiaryService diaryService;

    @PostMapping
    public BaseResponseDto<?> createChallenge(
            @RequestHeader("user") Long userId,
            @RequestBody CreateChallengeRequestDto requestDto
            ) {
        return BaseResponseDto.of(SuccessMessage.CREATED, challengeService.createChallenge(userId, requestDto));
    }

    @PostMapping("/register")
    public BaseResponseDto<?> createRegistration(
            @RequestHeader("user") Long userId,
            @RequestBody CreateRegistrationRequestDto requestDto
    ) {
        return BaseResponseDto.of(SuccessMessage.CREATED, challengeService.createRegistration(userId, requestDto.getVeggieId(), requestDto.getChallengeId()));
    }

    @PostMapping("/mission")
    public BaseResponseDto<?> createMissionPost(
            @RequestPart("image") MultipartFile image,
            @RequestParam("registrationId") Long registrationId,
            @RequestParam("content") String content
    ) {
        return BaseResponseDto.of(SuccessMessage.CREATED, missionPostService.createMissionPost(registrationId, content, image));
    }

    @GetMapping
    public BaseResponseDto<?> getMyChallengeList(
            @RequestHeader("user") Long userId
            ) {
        return BaseResponseDto.of(SuccessMessage.SUCCESS, challengeService.getMyChallengeList(userId));
    }

    @PostMapping("/search")
    public BaseResponseDto<?> searchChallengeList(
            @RequestHeader("user") Long userId,
            @RequestBody SearchChallengeListRequestDto requestDto
            ) {
        return BaseResponseDto.of(SuccessMessage.SUCCESS, challengeService.searchChallengeList(userId, requestDto.getDifficulties(), requestDto.getStatus(), requestDto.getKeyword()));
    }

    @GetMapping("/recommendation")
    public BaseResponseDto<?> getRecommendChallengeList(
            @RequestHeader("user") Long userId
    ) {
        return BaseResponseDto.of(SuccessMessage.SUCCESS, challengeService.getRecommendedChallengeList(userId));
    }

    @GetMapping("/{challengeId}")
    public BaseResponseDto<?> getChallengeDetail(
            @RequestHeader("user") Long userId,
            @PathVariable Long challengeId
    ) {
        return BaseResponseDto.of(SuccessMessage.SUCCESS, challengeService.getChallengeDetail(challengeId, userId));
    }

    @GetMapping("/mission/{challengeId}")
    public BaseResponseDto<?> getMyMissionPostList(
            @RequestHeader("user") Long userId,
            @PathVariable Long challengeId
    ) {
        return BaseResponseDto.of(SuccessMessage.SUCCESS, missionPostService.getMyMissionPosts(userId, challengeId));
    }

    @GetMapping("/mission/")
    public BaseResponseDto<?> getMissionPostList(
            @RequestParam Long challengeId,
            @RequestParam int stepNum
    ) {
        return BaseResponseDto.of(SuccessMessage.SUCCESS, missionPostService.getMissionPosts(challengeId, stepNum));
    }

    @GetMapping("/diary/{challengeId}")
    public BaseResponseDto<?> getDiaryPostList(
            @PathVariable Long challengeId
    ) {
        return BaseResponseDto.of(SuccessMessage.SUCCESS, missionPostService.getDiaryPosts(challengeId));
    }

    @DeleteMapping
    public BaseResponseDto<?> finishChallenge(
            @RequestHeader("user") Long userId,
            @RequestBody FinishChallengeRequestDto requestDto
            ) {
        return BaseResponseDto.of(SuccessMessage.SUCCESS, challengeService.finishChallenge(userId, requestDto));
    }

    @DeleteMapping("/complete")
    public BaseResponseDto<?> completeChallenge(
            @RequestHeader("user") Long userId,
            @RequestBody CompleteChallengeRequestDto requestDto
    ) {
        return BaseResponseDto.of(SuccessMessage.SUCCESS, missionPostService.completeChallenge(requestDto.getRegistrationId(), userId));
    }

    @PostMapping("/mission/{id}")
    public BaseResponseDto<?> likeMissionPost(
            @RequestHeader("user") Long userId,
            @PathVariable Long id
    ) {
        return BaseResponseDto.of(SuccessMessage.SUCCESS, missionPostService.likeMissionPost(userId, id));
    }

    @PostMapping("/diary/{id}")
    public BaseResponseDto<?> likeDiary(
            @RequestHeader("user") Long userId,
            @PathVariable Long id
    ) {
        return BaseResponseDto.of(SuccessMessage.SUCCESS, diaryService.likeDiary(id, userId));
    }

    @GetMapping("/test")
    public BaseResponseDto<?> test(
            @RequestHeader("user") Long userId
    ) {
        return BaseResponseDto.of(SuccessMessage.SUCCESS, challengeService.test(userId));
    }
}
