package com.example.farmusfarm.domain.veggie.repository;

import com.example.farmusfarm.domain.veggie.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
}
