package com.example.farmusfarm.domain.challenge.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Getter
public class CreateMissionPostResponseDto {

    private Long postId;
    private String challengeName;
    private int step;
    private String image;
}
