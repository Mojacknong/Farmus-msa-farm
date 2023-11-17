package com.example.farmusfarm.domain.veggieInfo.openfeign;

import com.example.farmusfarm.domain.veggieInfo.dto.res.GetStepNameResponseDto;
import com.example.farmusfarm.domain.veggieInfo.dto.res.VeggieInfoResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("crop-service")
public interface VeggieInfoFeignClient {

    @GetMapping(value = "/api/crop/info/{id}", consumes = "application/json")
    VeggieInfoResponseDto getVeggieInfo(@PathVariable String id);

    @GetMapping(value = "/api/crop/{id}/step/{step}", consumes = "application/json")
    GetStepNameResponseDto getVeggieInfoStepName(@PathVariable String id, @PathVariable int step);
}
