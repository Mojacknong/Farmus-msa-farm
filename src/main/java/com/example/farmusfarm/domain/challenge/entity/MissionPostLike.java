package com.example.farmusfarm.domain.challenge.entity;

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
@Entity(name = "mission_post_like")
public class MissionPostLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_post_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_post_id")
    private MissionPost missionPost;

    @Column(nullable = false)
    private Long userId;

    public static MissionPostLike createMissionPostLike(MissionPost missionPost, Long userId) {
        return MissionPostLike.builder()
                .missionPost(missionPost)
                .userId(userId)
                .build();
    }
}
