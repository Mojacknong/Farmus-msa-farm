package com.example.farmusfarm.domain.veggie.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Getter
public class GetCurrentMissionDto {

    private Long challengeId;
    private String veggieNickname;
    private int stepNum;
    private String stepName;
    private String color;
    private String detailId;
}
