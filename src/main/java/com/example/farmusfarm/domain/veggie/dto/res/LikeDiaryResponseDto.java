package com.example.farmusfarm.domain.veggie.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Getter
public class LikeDiaryResponseDto {

    private Boolean isLiked;
}
