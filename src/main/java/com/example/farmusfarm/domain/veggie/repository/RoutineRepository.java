package com.example.farmusfarm.domain.veggie.repository;

import com.example.farmusfarm.domain.veggie.entity.Routine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface RoutineRepository extends JpaRepository<Routine, Long> {

    // 채소 id로 루틴 전체 조회
    List<Routine> findAllByVeggieId(Long veggieId);
}
