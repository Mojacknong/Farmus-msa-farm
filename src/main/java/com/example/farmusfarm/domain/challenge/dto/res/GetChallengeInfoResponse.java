package com.example.farmusfarm.domain.challenge.dto.res;

import com.example.farmusfarm.domain.veggie.dto.res.GetDiaryResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Getter
public class GetChallengeInfoResponse {

    private String challengeName;
    private String veggieName;
    private String challengeDescription;
    private String veggieImage;

    private String difficulty;
    private int maxUser;
    private int currentUser;
    private int status;

    private List<Integer> achievement;
    private String stepName;
    private String stepTip;
    private List<String> stepImages;

    private List<GetDiaryResponseDto> diaries;
}
