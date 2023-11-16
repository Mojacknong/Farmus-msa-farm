package com.example.farmusfarm.domain.veggie.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Getter
public class GetDiaryResponseDto {

    private String profileImage;
    private String nickname;
    private String image;
    private String content;
    private String date;
}
