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
@Entity(name = "diary_like")
public class DiaryLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_like_id")
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id")
    private Diary diary;

    public static DiaryLike createDiaryLike(Long userId, Diary diary) {
        DiaryLike like = DiaryLike.builder()
                .userId(userId)
                .diary(diary)
                .build();

        diary.addLike(like);
        return like;
    }
}
