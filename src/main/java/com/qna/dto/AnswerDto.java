package com.qna.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AnswerDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Post {
        private long questionId;
        private long memberId;
        private String content;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Patch {

        private long answerId;
        private String content;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private long answerId;
        private long memberId;
        private long questionId;
        private String content;
        private String secret;
    }
}
