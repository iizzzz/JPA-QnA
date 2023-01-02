package com.qna.dto;

import com.qna.entity.Answer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class QuestionDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Post {
        private long memberId;
        private String title;
        private String content;
        private Boolean secret;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Patch {
        private long questionId;
        private String title;
        private String content;
        private Boolean secret;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private long questionId;
        private long memberId;
        private List<Answer> answers;
        private String title;
        private String content;
        private Boolean secret;
        private String status;

    }
}
