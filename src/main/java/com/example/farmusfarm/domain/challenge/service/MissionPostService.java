package com.example.farmusfarm.domain.challenge.service;

import com.example.farmusfarm.domain.challenge.repository.MissionPostImageRepository;
import com.example.farmusfarm.domain.challenge.repository.MissionPostLikeRepository;
import com.example.farmusfarm.domain.challenge.repository.MissionPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MissionPostService {

    private final MissionPostRepository missionPostRepository;
    private final MissionPostImageRepository missionPostImageRepository;
    private final MissionPostLikeRepository missionPostLikeRepository;
}
