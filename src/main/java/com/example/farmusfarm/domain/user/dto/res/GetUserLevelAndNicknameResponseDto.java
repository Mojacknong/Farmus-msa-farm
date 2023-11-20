package com.example.farmusfarm.domain.user.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetUserLevelAndNicknameResponseDto {

    private String level;
    private String nickname;
}
