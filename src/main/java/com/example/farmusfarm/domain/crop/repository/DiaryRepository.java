package com.example.farmusfarm.domain.crop.repository;

import com.example.farmusfarm.domain.crop.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
}
