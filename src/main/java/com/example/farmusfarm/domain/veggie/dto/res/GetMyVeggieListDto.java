package com.example.farmusfarm.domain.veggie.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Getter
public class GetMyVeggieListDto {

    private String userNickname;
    private String level;
    private List<GetVeggieInfoResponse> veggieList;
}
