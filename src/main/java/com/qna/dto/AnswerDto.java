package com.qna.dto;

import com.qna.entity.Answer;
import lombok.*;

public class AnswerDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Post {
        private String content;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Patch {

        private long answerId;
        private String content;

        public void setAnswerId(long answerId) {
            this.answerId = answerId;
        }
    }

    @Getter @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private long answerId;
        private String content;
        private Boolean secret;

        public static Response of(Answer answer) {
            return Response.builder()
                    .answerId(answer.getAnswerId())
                    .content(answer.getContent())
                    .secret(answer.getSecret())
                    .build();
        }
    }
}
