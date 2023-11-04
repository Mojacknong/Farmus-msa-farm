package com.example.farmusfarm.domain.crop.entity;

import com.example.farmusfarm.common.BaseEntity;
import com.example.farmusfarm.domain.challenge.entity.Registration;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@Table(indexes = {@Index(name = "crop_user_id_idx", columnList = "user_id")})
@Entity(name = "crop")
public class Crop extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crop_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String cropInfoId;

    @Column(nullable = false)
    private String cropNickname;

    @Column(nullable = false)
    private boolean isPresent;

    @OneToMany(mappedBy = "crop")
    @Builder.Default
    private List<Routine> routines = new ArrayList<>();

    @OneToOne(mappedBy = "crop")
    private Registration registration;

    @OneToMany(mappedBy = "crop")
    @Builder.Default
    private List<Diary> diaries = new ArrayList<>();

    public static Crop createCrop(Long userId, String cropInfoId, String cropNickname, boolean isPresent) {
        return Crop.builder()
                .userId(userId)
                .cropInfoId(cropInfoId)
                .cropNickname(cropNickname)
                .isPresent(isPresent)
                .build();
    }

    public void addRoutine(Routine routine) {
        routines.add(routine);
    }

    public void addDiary(Diary diary) {
        diaries.add(diary);
    }
}
