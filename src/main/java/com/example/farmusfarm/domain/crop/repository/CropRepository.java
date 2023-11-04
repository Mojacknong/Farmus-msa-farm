package com.example.farmusfarm.domain.crop.repository;

import com.example.farmusfarm.domain.crop.entity.Crop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CropRepository extends JpaRepository<Crop, Long> {
}
