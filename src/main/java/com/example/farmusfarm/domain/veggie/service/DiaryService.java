package com.example.farmusfarm.domain.veggie.service;

import com.example.farmusfarm.domain.veggie.repository.DiaryImageRepository;
import com.example.farmusfarm.domain.veggie.repository.DiaryLikeRepository;
import com.example.farmusfarm.domain.veggie.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final DiaryImageRepository diaryImageRepository;
    private final DiaryLikeRepository diaryLikeRepository;
}
