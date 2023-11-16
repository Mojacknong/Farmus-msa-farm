package com.example.farmusfarm.domain.veggieInfo.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class VeggieInfoResponseDto {

    private String veggieName = "대파";
    private String difficulty = "Easy";
    private String imageUrl = "https://farmuscropdata.s3.ap-northeast-2.amazonaws.com/image/%E1%84%91%E1%85%A1.png";
    private String grayImageUrl = "https://farmuscropdata.s3.ap-northeast-2.amazonaws.com/image/%E1%84%91%E1%85%A1_%E1%84%92%E1%85%B3%E1%86%A8.png";
    private int stepNum = 6;
}
