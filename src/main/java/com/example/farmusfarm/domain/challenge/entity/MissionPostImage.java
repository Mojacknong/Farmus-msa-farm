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
@Entity(name = "mission_post_image")
public class MissionPostImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_post_image_id")
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_post_id")
    private MissionPost missionPost;

    public static MissionPostImage createMissionPostImage(String imageUrl, MissionPost missionPost) {
         MissionPostImage newImage = MissionPostImage.builder()
                .imageUrl(imageUrl)
                .missionPost(missionPost)
                 .build();

         missionPost.addImage(newImage);
         return newImage;
    }
}
