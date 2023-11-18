package com.example.farmusfarm.domain.history.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateHistoryClubDetailRequestDto {

    private String image;
    private String veggieName;
    private String name;
    private String period;

    private List<HistoryClubPost> missionPosts;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class HistoryClubPost {
        private int stepNum;
        private String stepName;
        private String postImage;
        private String content;
        private String dateTime;
        private int likeNum;
    }
}
