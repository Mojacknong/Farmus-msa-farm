package com.example.farmusfarm.domain.veggie.service;

import com.example.farmusfarm.domain.veggie.dto.req.CreateDiaryRequestDto;
import com.example.farmusfarm.domain.veggie.dto.req.CreateVeggieRequestDto;
import com.example.farmusfarm.domain.veggie.dto.req.UpdateRoutineRequestDto;
import com.example.farmusfarm.domain.veggie.dto.res.CreateDiaryResponseDto;
import com.example.farmusfarm.domain.veggie.dto.res.CreateVeggieResponseDto;
import com.example.farmusfarm.domain.veggie.dto.res.GetTaskListResponseDto;
import com.example.farmusfarm.domain.veggie.dto.res.UpdateRoutineResponseDto;
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

    // 내 채소 추가
    public CreateVeggieResponseDto createVeggie(Long userId, CreateVeggieRequestDto requestDto) {
        // 채소 존재 여부 확인 -> OpenFeign
        // 채소 생성
        Veggie newVeggie = Veggie.createVeggie(userId, requestDto.getNickname(), requestDto.getVeggieInfoId());
        Veggie savedVeggie = veggieRepository.save(newVeggie);

        return CreateVeggieResponseDto.of(savedVeggie.getId());
    }

    // 채소 조회
    public Veggie getVeggie(Long veggieId) {
        return veggieRepository.findById(veggieId)
                .orElseThrow(() -> new EntityNotFoundException("채소가 존재하지 않습니다."));
    }

    // 채소 삭제
    public void deleteVeggie(Long veggieId) {
        veggieRepository.deleteById(veggieId);
    }

    // 유저 별 모든 채소 id 조회
    public List<Long> getVeggieIds(Long userId) {
        return veggieRepository.findAllVeggiesIdByUserId(userId);
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
    public Routine createRoutine(Date date, String content, int period, Veggie veggie) {
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
