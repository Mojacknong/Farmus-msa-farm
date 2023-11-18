package com.example.farmusfarm.domain.challenge.controller;

import com.example.farmusfarm.common.JwtTokenProvider;
import com.example.farmusfarm.common.S3Service;
import com.example.farmusfarm.domain.challenge.dto.req.CreateChallengeRequestDto;
import com.example.farmusfarm.domain.challenge.dto.req.CreateMissionPostRequestDto;
import com.example.farmusfarm.domain.challenge.dto.req.CreateRegistrationRequestDto;
import com.example.farmusfarm.domain.challenge.dto.req.SearchChallengeListRequestDto;
import com.example.farmusfarm.domain.challenge.service.ChallengeService;
import com.example.farmusfarm.domain.challenge.service.MissionPostService;
import com.example.farmusfarm.domain.veggie.service.DiaryService;
import com.example.farmusfarm.domain.veggieInfo.dto.res.GetStepNameResponseDto;
import com.example.farmusfarm.global.response.BaseResponseDto;
import com.example.farmusfarm.global.response.SuccessMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/farmclub")
public class ChallengeController {

    private final ChallengeService challengeService;
    private final MissionPostService missionPostService;
    private final JwtTokenProvider jwtTokenProvider;
    private final DiaryService diaryService;

    @PostMapping
    public BaseResponseDto<?> createChallenge(
            HttpServletRequest request,
            @RequestBody CreateChallengeRequestDto requestDto
            ) {
        Long userId = Long.valueOf(jwtTokenProvider.getUserId(request));
        return BaseResponseDto.of(SuccessMessage.CREATED, challengeService.createChallenge(userId, requestDto));
    }

    @PostMapping("/register")
    public BaseResponseDto<?> createRegistration(
            HttpServletRequest request,
            @RequestBody CreateRegistrationRequestDto requestDto
    ) {
        Long userId = Long.valueOf(jwtTokenProvider.getUserId(request));
        return BaseResponseDto.of(SuccessMessage.CREATED, challengeService.createRegistration(userId, requestDto.getVeggieId(), requestDto.getChallengeId()));
    }

    @PostMapping("/mission")
    public BaseResponseDto<?> createMissionPost(
            @RequestPart("content") CreateMissionPostRequestDto requestDto,
            @RequestPart("image") MultipartFile image
    ) {
        return BaseResponseDto.of(SuccessMessage.CREATED, missionPostService.createMissionPost(requestDto, image));
    }

    @GetMapping("")
    public BaseResponseDto<?> searchChallengeList(
            @RequestBody SearchChallengeListRequestDto requestDto
            ) {
        return BaseResponseDto.of(SuccessMessage.SUCCESS, challengeService.searchChallengeList(requestDto.getDifficulties(), requestDto.getStatus(), requestDto.getKeyword()));
    }

    @GetMapping("/{challengeId}")
    public BaseResponseDto<?> getChallengeDetail(
            HttpServletRequest request,
            @PathVariable Long challengeId
    ) {
        Long userId = Long.valueOf(jwtTokenProvider.getUserId(request));
        return BaseResponseDto.of(SuccessMessage.SUCCESS, challengeService.getChallengeDetail(challengeId, userId));
    }

    @PostMapping("/mission/{id}")
    public BaseResponseDto<?> likeMissionPost(
            HttpServletRequest request,
            @PathVariable Long id
    ) {
        Long userId = Long.valueOf(jwtTokenProvider.getUserId(request));
        return BaseResponseDto.of(SuccessMessage.SUCCESS, missionPostService.likeMissionPost(userId, id));
    }

    @PostMapping("/diary/{id}")
    public BaseResponseDto<?> likeDiary(
            HttpServletRequest request,
            @PathVariable Long id
    ) {
        Long userId = Long.valueOf(jwtTokenProvider.getUserId(request));
        return BaseResponseDto.of(SuccessMessage.SUCCESS, diaryService.likeDiary(id, userId));
    }

    @GetMapping("/test")
    public BaseResponseDto<?> test() {
        return BaseResponseDto.of(SuccessMessage.SUCCESS, challengeService.test());
    }
}
