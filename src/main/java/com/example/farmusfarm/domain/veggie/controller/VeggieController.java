package com.example.farmusfarm.domain.veggie.controller;

import com.example.farmusfarm.common.JwtTokenProvider;
import com.example.farmusfarm.domain.veggie.dto.req.*;
import com.example.farmusfarm.domain.veggie.service.DiaryService;
import com.example.farmusfarm.domain.veggie.service.VeggieService;
import com.example.farmusfarm.global.response.BaseResponseDto;
import com.example.farmusfarm.global.response.SuccessMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/veggie")
public class VeggieController {

    private final VeggieService veggieService;
    private final DiaryService diaryService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("")
    public BaseResponseDto<?> createVeggie(
            @RequestHeader("user") Long userId,
            @RequestBody CreateVeggieRequestDto requestDto
            ) {
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
            @RequestPart("image") final MultipartFile image,
            @RequestParam("veggieId") final Long veggieId,
            @RequestParam("content") final String content,
            @RequestParam("isOpen") final Boolean isOpen
            ) {
        return BaseResponseDto.of(SuccessMessage.CREATED, diaryService.createDiary(veggieId, content, isOpen, image));
    }



    @GetMapping("/{veggieId}")
    public BaseResponseDto<?> getVeggie(
            @PathVariable Long veggieId
    ) {
        return BaseResponseDto.of(SuccessMessage.SUCCESS, veggieService.getVeggieInfo(veggieId));
    }

    @GetMapping("")
    public BaseResponseDto<?> getMyVeggieList(
            @RequestHeader("user") Long userId
    ) {
        return BaseResponseDto.of(SuccessMessage.SUCCESS, veggieService.getMyVeggieList(userId));
    }

    @GetMapping("/registration/{veggieInfoId}")
    public BaseResponseDto<?> getMyVeggieListForRegistration(
            @RequestHeader("user") Long userId,
            @PathVariable String veggieInfoId
    ) {
        return BaseResponseDto.of(SuccessMessage.SUCCESS, veggieService.getMyVeggieListForRegistration(userId, veggieInfoId));
    }

    @GetMapping("/mission")
    public BaseResponseDto<?> getCurrentMissionList(
            @RequestHeader("user") Long userId
    ) {
        return BaseResponseDto.of(SuccessMessage.SUCCESS, veggieService.getCurrentMissionList(userId));
    }

    @GetMapping("/routine")
    public BaseResponseDto<?> getMonthRoutineList(
            @RequestHeader("user") Long userId,
            @RequestParam String date
    ) {
        return BaseResponseDto.of(SuccessMessage.SUCCESS, veggieService.getMonthRoutines(userId, LocalDate.parse(date)));
    }

    @GetMapping("/routine/today")
    public BaseResponseDto<?> getTodayRoutineList(
            @RequestHeader("user") Long userId
    ) {
        return BaseResponseDto.of(SuccessMessage.SUCCESS, veggieService.getDayRoutines(userId, LocalDate.now()));
    }

    @PatchMapping("/routine")
    public BaseResponseDto<?> updateRoutine(
            @RequestBody UpdateRoutineRequestDto requestDto
    ) {
        return BaseResponseDto.of(SuccessMessage.SUCCESS, veggieService.updateRoutine(requestDto));
    }

    @PatchMapping("/routine/check")
    public BaseResponseDto<?> updateRoutineCheck(
            @RequestBody CheckRoutineRequestDto requestDto
    ) {
        return BaseResponseDto.of(SuccessMessage.SUCCESS, veggieService.checkRoutine(requestDto));
    }

    @PatchMapping("")
    public BaseResponseDto<?> updateVeggie(
            @RequestBody UpdateVeggieRequestDto requestDto
    ) {
        return BaseResponseDto.of(SuccessMessage.SUCCESS, veggieService.updateVeggie(requestDto));
    }

    @GetMapping("/diary/{veggieId}")
    public BaseResponseDto<?> getVeggieDiaryList(
            @PathVariable Long veggieId
    ) {
        return BaseResponseDto.of(SuccessMessage.SUCCESS, diaryService.getVeggieDiaryList(veggieId));
    }

    @DeleteMapping
    public BaseResponseDto<?> deleteVeggie(
            @RequestHeader("user") Long userId,
            @RequestBody FinishFarmRequestDto requestDto
    ) {
        return BaseResponseDto.of(SuccessMessage.SUCCESS, veggieService.finishFarm(requestDto, userId));
    }

    @DeleteMapping("/{userId}")
    public void deleteAllVeggies(
            @PathVariable Long userId
    ) {
        veggieService.deleteAllVeggies(userId);
    }
}
