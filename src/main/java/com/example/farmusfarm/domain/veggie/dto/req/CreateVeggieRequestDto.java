package com.example.farmusfarm.domain.veggie.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateVeggieRequestDto {

    private String nickname;
    private String veggieInfoId;
    private String veggieName;
    private String veggieImage;
    private String birth;
}
