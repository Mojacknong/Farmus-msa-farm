package com.example.farmusfarm.domain.veggieInfo.openfeign;

import com.example.farmusfarm.domain.veggieInfo.dto.req.CreateHistoryClubDetailRequestDto;
import com.example.farmusfarm.domain.veggieInfo.dto.req.CreateHistoryDetailRequestDto;
import com.example.farmusfarm.domain.veggieInfo.dto.res.*;
import com.example.farmusfarm.global.response.BaseResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "crop", url = "http://3.36.221.140:8082")
public interface CropFeignClient {

    @GetMapping(value = "/api/crop/info/{id}", consumes = "application/json")
    VeggieInfoResponseDto getVeggieInfo(@PathVariable String id);

    @GetMapping(value = "/api/crop/{id}/step/{step}", consumes = "application/json")
    GetStepNameResponseDto getVeggieInfoStepName(@PathVariable String id, @PathVariable int step);

    @PostMapping(value = "/api/crop/history/detail", consumes = "application/json")
    CreateHistoryDetailResponseDto createHistoryDetail(
            @RequestHeader("user") Long userId,
            @RequestBody CreateHistoryDetailRequestDto requestDto
    );

    @PostMapping(value = "/api/crop/history/detail/club", consumes = "application/json")
    CreateHistoryClubDetailResponseDto createHistoryClubDetail(
            @RequestHeader("user") Long userId,
            @RequestBody CreateHistoryClubDetailRequestDto requestDto
    );

    @GetMapping(value = "/api/crop/{id}/info", consumes = "application/json")
    BaseResponseDto<List<GetStepsWithTipResponseDto>> getStepsWithTip(@PathVariable String id);

    @GetMapping(value = "/api/crop/{id}/steps", consumes = "application/json")
    BaseResponseDto<GetAllStepNameResponseDto> getAllStepName(@PathVariable String id);
}
