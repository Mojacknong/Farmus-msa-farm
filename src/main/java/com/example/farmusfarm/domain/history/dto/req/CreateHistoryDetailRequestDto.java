package com.example.farmusfarm.domain.history.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateHistoryDetailRequestDto {

    private String image;
    private String veggieName;
    private String name;
    private String period;

    private List<HistoryPost> diaryPosts;
    private HistoryPost farmResult;

    @AllArgsConstructor(staticName = "of")
    @NoArgsConstructor
    @Getter
    public static class HistoryPost {
        private String postImage;
        private String content;
        private String dateTime;
    }
}
