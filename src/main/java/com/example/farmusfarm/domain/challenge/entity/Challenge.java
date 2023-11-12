package com.example.farmusfarm.domain.challenge.entity;

import com.example.farmusfarm.common.BaseEntity;
import com.example.farmusfarm.domain.veggie.entity.Diary;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@SuperBuilder
@Entity(name = "challenge")
public class Challenge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_id")
    private Long id;

    @Column(nullable = false)
    private String veggieInfoId;

    @Column(nullable = false)
    private String veggieName;

    @Column(nullable = false)
    private String challengeName;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private String difficulty;

    @Column(nullable = false)
    private int maxStep;

    @Column(nullable = false)
    private int maxUser;

    @Column
    private String startedAt;

    @OneToMany(mappedBy = "challenge")
    @Builder.Default
    private List<Registration> registrations = new ArrayList<>();

    @OneToMany(mappedBy = "challenge")
    @Builder.Default
    private List<Diary> diaries = new ArrayList<>();

    public static Challenge createChallenge(String veggieInfoId, String veggieName, String difficulty, String image, String challengeName, int maxStep, int maxUser, String description) {
        return Challenge.builder()
                .veggieInfoId(veggieInfoId)
                .veggieName(veggieName)
                .image(image)
                .challengeName(challengeName)
                .description(description)
                .difficulty(difficulty)
                .maxStep(maxStep)
                .maxUser(maxUser)
                .build();
    }

    public void addRegistration(Registration registration) {
        registrations.add(registration);
    }

    public void addDiary(Diary diary) {
        diaries.add(diary);
    }

    public void setStartedAt(String startedAt) {
        this.startedAt = startedAt;
    }
}
