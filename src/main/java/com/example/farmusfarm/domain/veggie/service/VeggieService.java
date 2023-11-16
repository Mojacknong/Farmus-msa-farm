package com.example.farmusfarm.domain.veggie.service;

import com.example.farmusfarm.common.Utils;
import com.example.farmusfarm.domain.challenge.repository.RegistrationRepository;
import com.example.farmusfarm.domain.veggie.dto.req.*;
import com.example.farmusfarm.domain.veggie.dto.res.*;
import com.example.farmusfarm.domain.veggie.entity.Diary;
import com.example.farmusfarm.domain.veggie.entity.Routine;
import com.example.farmusfarm.domain.veggie.entity.Veggie;
import com.example.farmusfarm.domain.veggie.repository.RoutineRepository;
import com.example.farmusfarm.domain.veggie.repository.VeggieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class VeggieService {

    private final VeggieRepository veggieRepository;
    private final RoutineRepository routineRepository;
    private final RegistrationRepository registrationRepository;

    // 내 채소 추가
    public CreateVeggieResponseDto createVeggie(Long userId, CreateVeggieRequestDto requestDto) {
        // 채소 존재 여부 확인 -> OpenFeign
        // 채소 생성
        Veggie newVeggie = Veggie.createVeggie(userId, requestDto.getNickname(), requestDto.getVeggieInfoId(), requestDto.getVeggieImage(), requestDto.getBirth());
        Veggie savedVeggie = veggieRepository.save(newVeggie);

        return CreateVeggieResponseDto.of(savedVeggie.getId());
    }

    // 루틴 생성
    public CreateRoutineResponseDto createNewRoutine(CreateRoutineRequestDto requestDto) {
        Veggie veggie = getVeggie(requestDto.getVeggieId());
        Routine newRoutine = createRoutine(requestDto.getDate(), requestDto.getContent(), 0, veggie);

        return CreateRoutineResponseDto.of(newRoutine.getId());
    }

    // 채소 정보 조회
    public GetVeggieInfoResponse getVeggieInfo(Long veggieId) {
        Veggie veggie = getVeggie(veggieId);
        int age = Utils.compareLocalDate(LocalDate.now(), veggie.getBirth());

        return GetVeggieInfoResponse.of(veggieId, veggie.getVeggieInfoId(), veggie.getVeggieNickname(), age);
    }

    public GetMyVeggieListDto getMyVeggieList(Long userId) {
        List<Veggie> veggieList = getVeggieList(userId);

        // 페인으로 유저 닉네임 가져옴
        String userNickname = "파머";
        List<GetVeggieInfoResponse> result = veggieList.stream()
                .map(v -> GetVeggieInfoResponse.of(
                        v.getId(),
                        v.getVeggieInfoId(),
                        v.getVeggieNickname(),
                        Utils.compareLocalDate(LocalDate.now(), v.getBirth())))
                .collect(Collectors.toList());

        return GetMyVeggieListDto.of(userNickname, result);
    }

    // 채소 조회
    public Veggie getVeggie(Long veggieId) {
        return veggieRepository.findById(veggieId)
                .orElseThrow(() -> new EntityNotFoundException("채소가 존재하지 않습니다."));
    }

    // 채소 수정
    public UpdateVeggieResponseDto updateVeggie(UpdateVeggieRequestDto requestDto) {
        Veggie veggie = getVeggie(requestDto.getVeggieId());
        int age = Utils.compareLocalDate(veggie.getBirth(), LocalDate.now());

        veggie.updateVeggie(requestDto.getNickname(), requestDto.getBirth());

        return UpdateVeggieResponseDto.of(veggie.getId(), veggie.getVeggieNickname(), age);
    }

    // 채소 삭제
    public void deleteVeggie(Long veggieId) {
        Veggie veggie = getVeggie(veggieId);

        // 팜클럽에 참여중 일 경우
        if (getVeggie(veggieId).getRegistration() != null) {
            unregister(veggie);
        }

        veggieRepository.deleteById(veggieId);
    }

    // 유저 별 모든 채소 id 조회
    public List<Long> getVeggieIds(Long userId) {
        return veggieRepository.findAllVeggiesIdByUserId(userId);
    }

    // 유저 별 모든 채소 정보 조회 (홈)
    public List<Veggie> getVeggieList(Long userId) {
        return veggieRepository.findAllByUserId(userId);
    }

    // 팜클럽 등록 정보 삭제
    public void unregister(Veggie veggie) {
        registrationRepository.deleteById(veggie.getRegistration().getId());
        veggie.unregister();
    }

    // 유저 별 모든 현재 미션 조회

    // 유저 별 미션, 루틴 전체 조회
    public GetTaskListResponseDto getTaskList(Long userId) {
        // 전체 루틴 리스트
        List<Long> veggieIds = getVeggieIds(userId);
        List<List<Routine>> routines = veggieIds.stream().map(this::getRoutines).collect(Collectors.toList());

        // 전체 미션 리스트
        return GetTaskListResponseDto.of(userId);
    }

    // 루틴 생성
    public Routine createRoutine(String date, String content, int period, Veggie veggie) {
        Routine newRoutine = Routine.createRoutine(date, content, period, veggie);
        return routineRepository.save(newRoutine);
    }

    // 루틴 완료
    public boolean finishRoutine(Long routineId) {
        Routine routine = getRoutine(routineId);
        if (routine == null || routine.isDone()) {
            return false;
        } else {
            routine.updateDone();
            return true;
        }
    }

    // 루틴 조회
    public Routine getRoutine(Long routineId) {
        return routineRepository.findById(routineId)
                .orElseThrow(() -> new EntityNotFoundException("루틴이 존재하지 않습니다."));
    }

    // 채소 별 루틴 전체 조회
    public List<Routine> getRoutines(Long veggieId) {
        return routineRepository.findAllByVeggieId(veggieId);
    }

    // 루틴 수정
    public UpdateRoutineResponseDto updateRoutine(UpdateRoutineRequestDto requestDto) {
        Routine routine = getRoutine(requestDto.getRoutineId());
        routine.updatePeriod(requestDto.getPeriod());

        return UpdateRoutineResponseDto.of(routine.getId());
    }




}
