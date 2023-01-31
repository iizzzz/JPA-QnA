package com.qna.entity.contant;

import lombok.Getter;

@Getter
public enum QuestionStatus {
    REGISTRATION("질문 생성 상태"),
    ANSWERED("답변 완료 상태"),
    DELETE("질문 삭제 상태");

    @Getter
    private String status;

    QuestionStatus(String status) {
        this.status = status;
    }
}
