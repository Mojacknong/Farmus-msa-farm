package com.example.farmusfarm.domain.veggie.repository;

import com.example.farmusfarm.domain.veggie.entity.Veggie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VeggieRepository extends JpaRepository<Veggie, Long> {
}
