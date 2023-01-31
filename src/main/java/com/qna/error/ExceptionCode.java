package com.qna.error;

import lombok.Getter;

public enum ExceptionCode {
    MEMBER_NOT_FOUND(404, "Member not found"),
    MEMBER_EXISTS(409, "Member exists"),
    COFFEE_NOT_FOUND(404, "Coffee not found"),
    COFFEE_CODE_EXISTS(409, "Coffee Code exists"),
    ORDER_NOT_FOUND(404, "Order not found"),
    CANNOT_CHANGE_ORDER(403, "Order can not change"),
    NOT_IMPLEMENTATION(501, "Not Implementation"),
    INVALID_MEMBER_STATUS(400, "Invalid member status"),  // TO 추가된 부분
    QUESTION_NOT_FOUND(404, "Question Not Found"),
    QUESTION_DELETED(404, "Question Deleted"),
    NOT_AUTHORIZED(403, "Not Login Member"),
    VALID_AUTHORIZED(403, "Not Authorized Admin"),
    QUESTION_NOT_AUTHORIZED(405, "Not Authorized Question"),
    ANSWER_NOT_FOUND(404, "Answer Not Found"),
    ANSWER_DELETED(404, "Answer Deleted");

    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int code, String message) {
        this.status = code;
        this.message = message;
    }
}
