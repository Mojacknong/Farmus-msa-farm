package com.example.farmusfarm.domain.user.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Getter
public class GetAllUserResponseDto {

    List<UserInfoDto> allUserDtoList;
}
