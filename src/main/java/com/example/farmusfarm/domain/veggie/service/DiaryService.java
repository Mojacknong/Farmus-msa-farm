package com.example.farmusfarm.domain.veggie.service;

import com.example.farmusfarm.common.S3Service;
import com.example.farmusfarm.common.Utils;
import com.example.farmusfarm.domain.challenge.dto.res.LikeMissionPostResponseDto;
import com.example.farmusfarm.domain.challenge.entity.MissionPost;
import com.example.farmusfarm.domain.challenge.entity.MissionPostLike;
import com.example.farmusfarm.domain.veggie.dto.req.CreateDiaryRequestDto;
import com.example.farmusfarm.domain.veggie.dto.res.CreateDiaryResponseDto;
import com.example.farmusfarm.domain.veggie.dto.res.GetMyDiaryResponseDto;
import com.example.farmusfarm.domain.veggie.dto.res.LikeDiaryResponseDto;
import com.example.farmusfarm.domain.veggie.entity.Diary;
import com.example.farmusfarm.domain.veggie.entity.DiaryImage;
import com.example.farmusfarm.domain.veggie.entity.DiaryLike;
import com.example.farmusfarm.domain.veggie.entity.Veggie;
import com.example.farmusfarm.domain.veggie.repository.DiaryImageRepository;
import com.example.farmusfarm.domain.veggie.repository.DiaryLikeRepository;
import com.example.farmusfarm.domain.veggie.repository.DiaryRepository;
import com.example.farmusfarm.domain.veggie.repository.VeggieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final DiaryImageRepository diaryImageRepository;
    private final DiaryLikeRepository diaryLikeRepository;
    private final VeggieRepository veggieRepository;

    private final S3Service s3Service;

    // 일기 생성
    public CreateDiaryResponseDto createDiary(CreateDiaryRequestDto requestDto, MultipartFile image) {
        Veggie veggie = veggieRepository.findById(requestDto.getVeggieId())
                .orElseThrow(()-> new IllegalArgumentException("채소가 존재하지 않습니다."));

        Diary diary;
        if (veggie.getRegistration() != null && requestDto.getIsOpen()) {
            diary = Diary.createDiaryWithChallenge(requestDto.getContent(), true, veggie, veggie.getRegistration().getChallenge());
        } else {
            diary = Diary.createDiary(requestDto.getContent(), veggie);
        }
        Diary newDiary = diaryRepository.save(diary);

        // 이미지 추가
        createDiaryImage(newDiary, image);

        return CreateDiaryResponseDto.of(newDiary.getId());
    }

    // 작물 별 일기 목록 조회
    public List<GetMyDiaryResponseDto> getVeggieDiaryList(Long veggieId) {
        List<Diary> diaries = getDiaryByVeggieId(veggieId);
        return diaries.stream()
                .map(diary -> GetMyDiaryResponseDto.of(
                        diary.getId(),
                        diary.getContent(),
                        diary.getDiaryImages().get(0).getImageUrl(),
                        Utils.dateTimeToDateFormat(diary.getCreatedDate())
                        ))
                .collect(Collectors.toList());
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

    public LikeDiaryResponseDto likeDiary(Long diaryId, Long userId) {
        Diary diary = getDiary(diaryId);
        Optional<DiaryLike> like = diaryLikeRepository.findByDiaryIdAndUserId(diaryId, userId);
        Boolean result = like.isEmpty();

        if (result) {
            diaryLikeRepository.save(DiaryLike.createDiaryLike(userId, diary));
        } else {
            diaryLikeRepository.delete(like.get());
        }

        return LikeDiaryResponseDto.of(result);
    }

    public void createDiaryImage(Diary diary, MultipartFile image) {
        String imageUrl = uploadImage(image);
        DiaryImage diaryImage = DiaryImage.createDiaryImage(imageUrl, diary);
        diaryImageRepository.save(diaryImage);
    }

    public String uploadImage(MultipartFile image) {
        return s3Service.uploadImage(image, "diary");
    }
}
