package com.example.farmusfarm.domain.veggie.entity;


import com.example.farmusfarm.common.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@Entity(name = "diary_image")
public class DiaryImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_image_id")
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id")
    private Diary diary;

    public static DiaryImage createDiaryImage(String imageUrl, Diary diary) {
        DiaryImage diaryImage = DiaryImage.builder()
                .imageUrl(imageUrl)
                .diary(diary)
                .build();

        diary.addImage(diaryImage);
        return diaryImage;
    }
}
