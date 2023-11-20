package com.example.farmusfarm.domain.user.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Getter
public class UserInfoDto {

    private String nickName;
    private String imageUrl;
    private Long id;
}
