package com.example.farmusfarm.domain.veggie.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Getter
public class GetDayRoutinesResponseDto {

    private Long veggieId;
    private String veggieNickname;
    private String color;
    private List<GetDayRoutinesDto> routineList;
}
