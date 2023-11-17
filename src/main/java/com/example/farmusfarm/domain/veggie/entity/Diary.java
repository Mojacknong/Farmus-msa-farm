package com.example.farmusfarm.domain.veggie.entity;

import com.example.farmusfarm.common.BaseEntity;
import com.example.farmusfarm.domain.challenge.entity.Challenge;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@Entity(name = "diary")
public class Diary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_id")
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column
    private boolean isOpen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veggie_id")
    private Veggie veggie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @OneToMany(mappedBy = "diary")
    @Builder.Default
    private List<DiaryLike> diaryLikes = new ArrayList<>();

    @OneToMany(mappedBy = "diary")
    @Builder.Default
    private List<DiaryImage> diaryImages = new ArrayList<>();

    public static Diary createDiary(String content, Veggie veggie) {
        Diary diary = Diary.builder()
                .content(content)
                .isOpen(false)
                .veggie(veggie)
                .build();

        veggie.addDiary(diary);
        return diary;
    }

    public static Diary createDiaryWithChallenge(String content, boolean isOpen, Veggie veggie, Challenge challenge) {
        Diary diary = Diary.builder()
                .content(content)
                .isOpen(isOpen)
                .veggie(veggie)
                .challenge(challenge)
                .build();

        veggie.addDiary(diary);
        challenge.addDiary(diary);
        return diary;
    }

    public void addImage(DiaryImage diaryImage) {
        diaryImages.add(diaryImage);
    }

    public void addLike(DiaryLike diaryLike) {
        diaryLikes.add(diaryLike);
    }
}
