package com.example.farmusfarm.domain.user.openfeign;

import com.example.farmusfarm.domain.user.dto.res.GetAllUserResponseDto;
import com.example.farmusfarm.global.response.BaseResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "user", url = "http://3.38.2.59:8081")
public interface UserFeignClient {

    @GetMapping(value = "/api/user/all-user", consumes = "application/json")
    BaseResponseDto<GetAllUserResponseDto> getAllUser();
}
