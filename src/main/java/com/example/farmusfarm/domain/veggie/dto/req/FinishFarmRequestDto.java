package com.example.farmusfarm.domain.veggie.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FinishFarmRequestDto {

    private Long veggieId;
    private Boolean isHarvested;
}
