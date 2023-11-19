package com.example.farmusfarm.domain.challenge.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Getter
public class GetMissionPostResponseDto {

    private Long postId;

    private String profileImage;
    private String nickName;
    private String date;
    private String image;
    private String content;
    private int like;
}
