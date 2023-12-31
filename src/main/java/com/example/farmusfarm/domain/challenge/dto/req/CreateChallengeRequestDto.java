package com.example.farmusfarm.domain.challenge.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateChallengeRequestDto {

    private Long myVeggieId;
    private String veggieInfoId;

    private String challengeName;
    private int maxUser;
    private String description;
}
