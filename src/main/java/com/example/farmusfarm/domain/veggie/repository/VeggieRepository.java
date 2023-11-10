package com.example.farmusfarm.domain.veggie.repository;

import com.example.farmusfarm.domain.veggie.entity.Veggie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VeggieRepository extends JpaRepository<Veggie, Long> {

    // 유저 별 채소 id 전체 조회
    @Query("select v.id from veggie v where v.userId = :userId")
    List<Long> findAllVeggiesIdByUserId(Long userId);
}
