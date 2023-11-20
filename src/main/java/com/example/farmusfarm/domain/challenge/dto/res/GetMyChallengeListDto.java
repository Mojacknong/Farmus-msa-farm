package com.example.farmusfarm.domain.challenge.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetMyChallengeListDto {

    private Long challengeId;
    private Long registrationId;
    private String image;
    private String grayImage;
}
