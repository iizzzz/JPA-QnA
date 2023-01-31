package com.qna.dto;

import com.qna.entity.Question;
import com.qna.entity.contant.QuestionStatus;
import lombok.*;
import java.time.LocalDateTime;

public class QuestionDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Post {
        private String title;
        private String content;
        private Boolean secret;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Patch {
        private long questionId;
        private String title;
        private String content;
        private Boolean secret;

        public void setQuestionId(long questionId) {
            this.questionId = questionId;
        }
    }

    @Getter @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SingleResponse {
        private long questionId;
        private String title;
        private String content;
        private Boolean secret;
        private QuestionStatus status;
        private int views;
        private LocalDateTime createdAt;
        private String writer;

        public static SingleResponse of(Question question) {
            return SingleResponse.builder()
                    .questionId(question.getQuestionId())
                    .title(question.getTitle())
                    .content(question.getContent())
                    .secret(question.getSecret())
                    .status(question.getStatus())
                    .views(question.getViews())
                    .writer(question.getMember().getName())
                    .createdAt(question.getCreatedAt())
                    .build();
        }
    }

    @Getter @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MultiResponse {
        private long questionId;
        private String title;
        private Boolean secret;
        private QuestionStatus status;
        private int views;
        private String writer;
        private LocalDateTime createAt;

        public static MultiResponse of(Question question) {
            return MultiResponse.builder()
                    .questionId(question.getQuestionId())
                    .title(question.getTitle())
                    .secret(question.getSecret())
                    .status(question.getStatus())
                    .views(question.getViews())
                    .writer(question.getMember().getName())
                    .createAt(question.getCreatedAt())
                    .build();
        }
    }
}
