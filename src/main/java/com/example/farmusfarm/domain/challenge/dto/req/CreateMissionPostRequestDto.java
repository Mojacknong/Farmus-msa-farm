package com.example.farmusfarm.domain.challenge.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateMissionPostRequestDto {

    private Long registrationId;
    private String content;
}
