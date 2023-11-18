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
@Table(indexes = {@Index(name = "registration_user_id_idx", columnList = "user_id")})
@Entity(name = "registration")
public class    Registration extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registration_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private int currentStep;

    @Column(nullable = false)
    private String currentStepName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @OneToMany(mappedBy = "registration", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<MissionPost> missionPosts = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veggie_id")
    private Veggie veggie;

    public static Registration createRegistration(Long userId, String currentStepName, Challenge challenge, Veggie veggie) {
        Registration newRegistration = Registration.builder()
                .userId(userId)
                .currentStep(0)
                .currentStepName(currentStepName)
                .challenge(challenge)
                .veggie(veggie)
                .build();

        challenge.addRegistration(newRegistration);
        veggie.register(newRegistration);
        return newRegistration;
    }

    public void addMissionPost(MissionPost missionPost) {
        missionPosts.add(missionPost);
    }

    public void updateCurrentStep(String nextStepName) {
        this.currentStep = currentStep + 1;
        this.currentStepName = nextStepName;
    }
}
