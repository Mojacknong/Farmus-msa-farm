package com.example.farmusfarm.domain.veggie.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateRoutineRequestDto {

    private Long routineId;
    private int period;
}
