package com.example.farmusfarm.domain.challenge.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Getter
public class CompleteChallengeResponseDto {

    private Long veggieId;

    private String image;
    private String challengeName;

    private int day;
    private int mission;
    private int diary;
}
