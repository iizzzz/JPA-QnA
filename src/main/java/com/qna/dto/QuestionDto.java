package com.qna.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
        private long memberId;
        private String title;
        private String content;
        private Boolean secret;

        public void setQuestionId(long questionId) {
            this.questionId = questionId;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private long questionId;
        private long memberId;
        private long answerId;
        private String title;
        private String content;
        private Boolean secret;
        private String status;

    }
}
