package com.example.farmusfarm.domain.challenge.entity;

import com.example.farmusfarm.common.BaseEntity;
import com.example.farmusfarm.domain.crop.entity.Diary;
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
    private String cropId;

    @Column(nullable = false)
    private String cropName;

    @Column(nullable = false)
    private String challengeName;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private String difficulty;

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

    public static Challenge createChallenge(String crop_id, String crop_name, String challenge_name, String description, String image, String difficulty, int max_user) {
        return Challenge.builder()
                .cropId(crop_id)
                .cropName(crop_name)
                .challengeName(challenge_name)
                .description(description)
                .image(image)
                .difficulty(difficulty)
                .maxUser(max_user)
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
