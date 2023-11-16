package com.example.farmusfarm.domain.veggie.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateVeggieRequestDto {

    private Long veggieId;
    private String nickname;
    private String birth;
}
