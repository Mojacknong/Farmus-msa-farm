package com.example.farmusfarm.domain.challenge.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SearchChallengeListRequestDto {

    private List<String> difficulties;
    private String status;
    private String keyword;
}
