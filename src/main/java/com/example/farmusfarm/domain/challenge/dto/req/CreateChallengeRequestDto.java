package com.example.farmusfarm.domain.challenge.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateChallengeRequestDto {

    private String veggieInfoId;
    private String veggieName;
    private String difficulty;
    private String image;

    private String challengeName;
    private int maxUser;
    private String description;
}
