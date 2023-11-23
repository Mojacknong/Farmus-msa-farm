package com.example.farmusfarm.domain.veggie.service;

import com.example.farmusfarm.common.Colors;
import com.example.farmusfarm.common.Utils;
import com.example.farmusfarm.domain.challenge.entity.Registration;
import com.example.farmusfarm.domain.challenge.repository.RegistrationRepository;
import com.example.farmusfarm.domain.user.dto.res.GetUserLevelAndNicknameResponseDto;
import com.example.farmusfarm.domain.user.openfeign.UserFeignClient;
import com.example.farmusfarm.domain.veggieInfo.dto.req.CreateHistoryDetailRequestDto;
import com.example.farmusfarm.domain.veggie.dto.req.*;
import com.example.farmusfarm.domain.veggie.dto.res.*;
import com.example.farmusfarm.domain.veggie.entity.Diary;
import com.example.farmusfarm.domain.veggie.entity.Routine;
import com.example.farmusfarm.domain.veggie.entity.Veggie;
import com.example.farmusfarm.domain.veggie.repository.RoutineRepository;
import com.example.farmusfarm.domain.veggie.repository.VeggieRepository;
import com.example.farmusfarm.domain.veggieInfo.openfeign.CropFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class VeggieService {

    private final VeggieRepository veggieRepository;
    private final RoutineRepository routineRepository;
    private final RegistrationRepository registrationRepository;

    private final CropFeignClient cropFeignClient;
    private final UserFeignClient userFeignClient;

    // 내 채소 추가
    public CreateVeggieResponseDto createVeggie(Long userId, CreateVeggieRequestDto requestDto) {
        // 채소 존재 여부 확인 -> OpenFeign
        // 채소 생성
        Veggie newVeggie = Veggie.createVeggie(userId, requestDto.getVeggieInfoId(), requestDto.getVeggieName(), requestDto.getNickname(), requestDto.getVeggieImage(), requestDto.getBirth(), getRandomColorCode());
        Veggie savedVeggie = veggieRepository.save(newVeggie);

        return CreateVeggieResponseDto.of(savedVeggie.getId());
    }

    // 루틴 생성
    public CreateRoutineResponseDto createNewRoutine(CreateRoutineRequestDto requestDto) {
        Veggie veggie = getVeggie(requestDto.getVeggieId());
        Routine newRoutine = createRoutine(requestDto.getDate(), requestDto.getContent(), 0, veggie);

        return CreateRoutineResponseDto.of(newRoutine.getId(), newRoutine.getDate().toString(), newRoutine.getContent(), newRoutine.getPeriod(), false);
    }

    // 채소 정보 조회
    public GetVeggieInfoResponse getVeggieInfo(Long veggieId) {
        Veggie veggie = getVeggie(veggieId);
        int age = Utils.compareLocalDate(LocalDate.now(), veggie.getBirth());

        return GetVeggieInfoResponse.of(veggieId, veggie.getVeggieNickname(), veggie.getVeggieImage(), age);
    }

    public GetMyVeggieListDto getMyVeggieList(Long userId) {
        List<Veggie> veggieList = getVeggieList(userId);

        // 페인으로 유저 닉네임 가져옴
        GetUserLevelAndNicknameResponseDto response = userFeignClient.getUserLevelAndNickname(userId);
        List<GetVeggieInfoResponse> result = veggieList.stream()
                .map(v -> GetVeggieInfoResponse.of(
                        v.getId(),
                        v.getVeggieNickname(),
                        v.getVeggieImage(),
                        Utils.compareLocalDate(LocalDate.now(), v.getBirth())))
                .collect(Collectors.toList());

        return GetMyVeggieListDto.of(response.getNickname(), response.getLevel(), response.getMotivation(), result);
    }

    public List<GetRegistrationVeggieListResponseDto> getMyVeggieListForRegistration(Long userId, String veggieInfoId) {

        List<Veggie> veggieList = veggieRepository.findAllByUserId(userId);

        return veggieList.stream()
                .filter(v -> v.getRegistration() == null && v.getVeggieInfoId().equals(veggieInfoId))
                .map(v -> GetRegistrationVeggieListResponseDto.of(
                        v.getId(),
                        v.getVeggieName(),
                        v.getVeggieNickname()
                )).collect(Collectors.toList());
    }

    // 유저 별 모든 현재 미션 조회
    public List<GetCurrentMissionDto> getCurrentMissionList(Long userId) {
        List<Veggie> veggieList = getVeggieList(userId);
        return veggieList.stream()
                .filter(v -> v.getRegistration() != null)
                .map( v -> GetCurrentMissionDto.of(
                        v.getRegistration().getChallenge().getId(),
                        v.getVeggieNickname(),
                        v.getRegistration().getCurrentStep(),
                        v.getRegistration().getCurrentStepName(),
                        v.getColor()))
                .collect(Collectors.toList());
    }

    // 유저 별 미션, 루틴 전체 조회
    public List<List<GetDayRoutinesResponseDto>> getMonthRoutines(Long userId, LocalDate date) {

        int days = date.lengthOfMonth();

        // 이번 달 만큼의 루틴 리스트
        List<List<GetDayRoutinesResponseDto>> result = new ArrayList<>();

        // 전체 루틴 리스트
        List<Veggie> veggieList = getVeggieList(userId);

        // 이번 달 반복문
        for (int i = 1; i <= days; i++) {
            LocalDate currentDate = LocalDate.of(date.getYear(), date.getMonth(), i);
            result.add(veggieList.stream().map(v -> getRoutineInfo(v, currentDate)).collect(Collectors.toList()));
        }

        return result;

        //return veggieList.stream().map(v -> getRoutineInfo(v, date)).collect(Collectors.toList());
    }

    public List<GetDayRoutinesResponseDto> getDayRoutines(Long userId, LocalDate date) {
        List<Veggie> veggieList = getVeggieList(userId);
        return veggieList.stream().map(v -> getRoutineInfo(v, date)).collect(Collectors.toList());
    }

    // 루틴 수정
    public UpdateRoutineResponseDto updateRoutine(UpdateRoutineRequestDto requestDto) {
        Routine routine = getRoutine(requestDto.getRoutineId());
        routine.updatePeriod(requestDto.getPeriod());

        return UpdateRoutineResponseDto.of(routine.getId());
    }

    // 루틴 체크
    public CheckRoutineResponseDto checkRoutine(CheckRoutineRequestDto requestDto) {
        Routine routine = getRoutine(requestDto.getRoutineId());
        routine.updateDone();

        if (routine.getPeriod() != 0) {
            Routine newRoutine = createRoutine(
                    routine.getDate().plusDays(routine.getPeriod()).toString(),
                    routine.getContent(),
                    routine.getPeriod(),
                    routine.getVeggie()
            );
            routineRepository.save(newRoutine);
        }

        return CheckRoutineResponseDto.of(routine.getId(), routine.getIsDone());
    }

    // 홈파밍 종료
    public FinishFarmResponseDto finishFarm(FinishFarmRequestDto requestDto, Long userId) {
        Veggie veggie = getVeggie(requestDto.getVeggieId());

        Registration registration = veggie.getRegistration();

        // 재배 성공
        if (requestDto.getIsHarvested()) {
            if (registration != null) {
                return FinishFarmResponseDto.of(false);
            }
            List<CreateHistoryDetailRequestDto.HistoryPost> historyPosts = getHistoryPosts(veggie.getDiaries());

            CreateHistoryDetailRequestDto detailRequestDto = new CreateHistoryDetailRequestDto(
                    veggie.getVeggieImage(),
                    veggie.getVeggieNickname(),
                    "",
                    veggie.getBirth() + "~" + LocalDate.now(),
                    historyPosts,
                    null
            );

            // 히스토리 생성 요청
            cropFeignClient.createHistoryDetail(userId, detailRequestDto);
        } else {
            if (registration != null) {
                unregister(registration);
            }
        }

        deleteVeggie(requestDto.getVeggieId());
        return FinishFarmResponseDto.of(true);
    }

    public void deleteAllVeggies(Long userId) {
        veggieRepository.deleteAllByUserId(userId);
    }

    // -------------------- api --------------------

    public List<CreateHistoryDetailRequestDto.HistoryPost> getHistoryPosts(List<Diary> diaries) {
        return diaries.stream()
                .map(d -> CreateHistoryDetailRequestDto.HistoryPost.of(
                        d.getDiaryImages().get(0).getImageUrl(), d.getContent(), Utils.dateTimeFormat(d.getCreatedDate())))
                .collect(Collectors.toList());
    }

    // 채소 조회
    public Veggie getVeggie(Long veggieId) {
        return veggieRepository.findById(veggieId)
                .orElseThrow(() -> new EntityNotFoundException("채소가 존재하지 않습니다."));
    }

    // 채소 수정
    public UpdateVeggieResponseDto updateVeggie(UpdateVeggieRequestDto requestDto) {
        Veggie veggie = getVeggie(requestDto.getVeggieId());
        int age = Utils.compareLocalDate(LocalDate.now(), veggie.getBirth());

        veggie.updateVeggie(requestDto.getNickname(), requestDto.getBirth());

        return UpdateVeggieResponseDto.of(veggie.getId(), veggie.getVeggieNickname(), age);
    }

    // 채소 삭제
    public void deleteVeggie(Long veggieId) {
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
    public void unregister(Registration registration) {
        registration.getVeggie().unregister();
        registration.getChallenge().getRegistrations().remove(registration);
        registrationRepository.deleteById(registration.getId());
    }

    // 루틴 생성
    public Routine createRoutine(String date, String content, int period, Veggie veggie) {
        Routine newRoutine = Routine.createRoutine(date, content, period, veggie);
        return routineRepository.save(newRoutine);
    }

    // 루틴 완료
    public boolean finishRoutine(Long routineId) {
        Routine routine = getRoutine(routineId);
        if (routine == null || routine.getIsDone()) {
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

    // date에 맞는 루틴만 조회
    public GetDayRoutinesResponseDto getRoutineInfo(Veggie veggie, LocalDate date) {
        List<Routine> routines = veggie.getRoutines();
        List<GetDayRoutinesDto> result = routines.stream()
                .filter(r -> r.getDate().isEqual(date))
                .map(r -> GetDayRoutinesDto.of(r.getId(), r.getContent(), r.getPeriod(), r.getIsDone()))
                .collect(Collectors.toList());

        return GetDayRoutinesResponseDto.of(veggie.getId(), veggie.getVeggieNickname(), veggie.getColor(),result);
    }

    public String getRandomColorCode() {
        int random = (int) (Math.random() * 3);
        return Colors.values()[random].getHexCode();
    }
}
