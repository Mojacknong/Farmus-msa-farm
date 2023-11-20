package com.example.farmusfarm.domain.challenge.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Getter
public class GetMyMissionPostResponseDto {

    private Long postId;

    private int stepNum;
    private String stepName;

    private String date;
    private String image;
    private String content;
    private int likeNum;
}
