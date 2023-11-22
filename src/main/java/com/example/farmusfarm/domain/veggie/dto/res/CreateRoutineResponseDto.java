package com.example.farmusfarm.domain.veggie.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Getter
public class CreateRoutineResponseDto {

    private Long id;
    private String date;
    private String content;
    private int period;
    private Boolean isDone;
}
