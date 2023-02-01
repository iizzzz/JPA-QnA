package com.qna.dto;

import com.qna.entity.contant.Role;
import lombok.Getter;

import java.util.List;

@Getter
public class LoginDto {
    private String email;
    private String password;

    public static class Response {
        private long memberId;
        private String email;
        private String name;
        private List<Role> roles;
    }
}
