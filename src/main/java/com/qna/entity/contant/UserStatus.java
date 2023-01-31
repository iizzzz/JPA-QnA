package com.qna.entity.contant;

import lombok.Getter;

@Getter
public enum UserStatus {

    ACTIVE("활동중"),
    SLEEP("휴면 상태"),
    QUIT("탈퇴 상태");

    private String status;

    UserStatus(String status) {
        this.status = status;
    }
}
