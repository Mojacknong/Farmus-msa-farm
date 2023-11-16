package com.example.farmusfarm.domain.veggie.controller;

import com.example.farmusfarm.common.JwtTokenProvider;
import com.example.farmusfarm.domain.veggie.dto.req.CreateDiaryRequestDto;
import com.example.farmusfarm.domain.veggie.dto.req.CreateRoutineRequestDto;
import com.example.farmusfarm.domain.veggie.dto.req.CreateVeggieRequestDto;
import com.example.farmusfarm.domain.veggie.service.DiaryService;
import com.example.farmusfarm.domain.veggie.service.VeggieService;
import com.example.farmusfarm.global.response.BaseResponseDto;
import com.example.farmusfarm.global.response.SuccessMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/veggie")
public class VeggieController {

    private final VeggieService veggieService;
    private final DiaryService diaryService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("")
    public BaseResponseDto<?> createVeggie(
            HttpServletRequest request,
            @RequestBody CreateVeggieRequestDto requestDto
            ) {
        Long userId = Long.valueOf(jwtTokenProvider.getUserId(request));
        return BaseResponseDto.of(SuccessMessage.CREATED, veggieService.createVeggie(userId, requestDto));
    }

    @PostMapping("/routine")
    public BaseResponseDto<?> createRoutine(
            @RequestBody CreateRoutineRequestDto requestDto
    ) {
        return BaseResponseDto.of(SuccessMessage.CREATED, veggieService.createNewRoutine(requestDto));
    }

    @PostMapping("/diary")
    public BaseResponseDto<?> createDiary(
            @RequestBody CreateDiaryRequestDto requestDto
    ) {

    }
}
