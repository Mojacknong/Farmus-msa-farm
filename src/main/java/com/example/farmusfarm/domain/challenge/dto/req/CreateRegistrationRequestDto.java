package com.example.farmusfarm.domain.challenge.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateRegistrationRequestDto {

    private Long challengeId;
    private Long veggieId;
}
