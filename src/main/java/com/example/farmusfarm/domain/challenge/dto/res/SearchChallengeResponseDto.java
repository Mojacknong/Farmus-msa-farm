package com.example.farmusfarm.domain.challenge.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Getter
public class SearchChallengeResponseDto {

    private Long challengeId;
    private String veggieName;
    private String challengeName;
    private String image;
    private String difficulty;
    private int maxUser;
    private int currentUser;
    private String status;
}
