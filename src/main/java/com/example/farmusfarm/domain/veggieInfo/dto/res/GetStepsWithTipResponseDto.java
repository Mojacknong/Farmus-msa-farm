package com.example.farmusfarm.domain.veggieInfo.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Getter
public class GetStepsWithTipResponseDto {

    private String stepName;
    private String tip;
}
