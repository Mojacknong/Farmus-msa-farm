package com.example.farmusfarm.domain.challenge.entity;


import com.example.farmusfarm.common.BaseEntity;
import com.example.farmusfarm.domain.veggie.entity.Veggie;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@Entity(name = "registration")
public class Registration extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registration_id")
    private Long id;

    @Column(nullable = false)
    private int currentStep;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @OneToMany(mappedBy = "registration")
    @Builder.Default
    private List<MissionPost> missionPosts = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veggie_id")
    private Veggie veggie;

    public static Registration createRegistration(int currentStep, Challenge challenge, Veggie veggie) {
        return Registration.builder()
                .currentStep(currentStep)
                .challenge(challenge)
                .veggie(veggie)
                .build();
    }

    public void addMissionPost(MissionPost missionPost) {
        missionPosts.add(missionPost);
    }

    public void updateCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }
}
