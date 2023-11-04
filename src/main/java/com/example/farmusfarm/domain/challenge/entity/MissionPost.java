package com.example.farmusfarm.domain.challenge.entity;

import com.example.farmusfarm.common.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@Entity(name = "mission_post")
public class MissionPost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_post_id")
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int step;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_id")
    private Registration registration;

    @OneToMany(mappedBy = "missionPost")
    @Builder.Default
    private List<MissionPostLike> missionPostLikes = new ArrayList<>();

    @OneToMany(mappedBy = "missionPost")
    @Builder.Default
    private List<MissionPostImage> missionPostImages = new ArrayList<>();

    public static MissionPost createMissionPost(String content, int step, Registration registration) {
        return MissionPost.builder()
                .content(content)
                .step(step)
                .registration(registration)
                .build();
    }

    public void addImage(MissionPostImage missionPostImage) {
        missionPostImages.add(missionPostImage);
    }

    public void addLike(MissionPostLike missionPostLike) {
        missionPostLikes.add(missionPostLike);
    }

    public void removeLike(MissionPostLike missionPostLike) {
        missionPostLikes.remove(missionPostLike);
    }
}
