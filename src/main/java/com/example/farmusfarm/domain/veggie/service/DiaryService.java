package com.example.farmusfarm.domain.veggie.service;

import com.example.farmusfarm.domain.veggie.dto.req.CreateDiaryRequestDto;
import com.example.farmusfarm.domain.veggie.dto.res.CreateDiaryResponseDto;
import com.example.farmusfarm.domain.veggie.entity.Diary;
import com.example.farmusfarm.domain.veggie.entity.Veggie;
import com.example.farmusfarm.domain.veggie.repository.DiaryImageRepository;
import com.example.farmusfarm.domain.veggie.repository.DiaryLikeRepository;
import com.example.farmusfarm.domain.veggie.repository.DiaryRepository;
import com.example.farmusfarm.domain.veggie.repository.VeggieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final DiaryImageRepository diaryImageRepository;
    private final DiaryLikeRepository diaryLikeRepository;
    private final VeggieRepository veggieRepository;

    // 일기 생성
    public CreateDiaryResponseDto createDiary(Long veggieId, CreateDiaryRequestDto requestDto) {
        Veggie veggie = veggieRepository.findById(veggieId)
                .orElseThrow(()-> new IllegalArgumentException("채소가 존재하지 않습니다."));

        Diary diary;
        if (veggie.getRegistration() == null) {
            diary = Diary.createDiary(requestDto.getContent(), veggie);
        } else {
            diary = Diary.createDiaryWithChallenge(requestDto.getContent(), requestDto.isOpen() , veggie, veggie.getRegistration().getChallenge());
        }

        // 이미지 추가

        veggie.addDiary(diary);

        return CreateDiaryResponseDto.of(veggie.getId());
    }

    // 일기 조회
    public Diary getDiary(Long diaryId) {
        return diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("일기가 존재하지 않습니다."));
    }

    // 채소 별 일기 조회
    public List<Diary> getDiaryByVeggieId(Long veggieId) {
        return diaryRepository.findAllByVeggieId(veggieId);
    }

    // 일기 삭제
    public void deleteDiary(Long diaryId) {
        diaryRepository.deleteById(diaryId);
    }
}
