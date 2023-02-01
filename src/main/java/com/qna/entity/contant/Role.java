package com.qna.entity.contant;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN("관리자"), USER("일반 유저");

    private String role;

    Role(String role) {
        this.role = role;
    }
}
