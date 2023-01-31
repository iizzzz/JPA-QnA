package com.qna.dto;

import com.qna.entity.Member;
import com.qna.entity.contant.UserStatus;
import com.qna.utils.NotSpace;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

public class MemberDto {
    @Getter
    @AllArgsConstructor
    public static class Post {
        @NotBlank
        @Email
        private String email;

        private String password;

        @NotBlank(message = "이름은 공백이 아니어야 합니다.")
        private String name;

        @Pattern(regexp = "^010-\\d{3,4}-\\d{4}$",
                message = "휴대폰 번호는 010으로 시작하는 11자리 숫자와 '-'로 구성되어야 합니다.")
        private String phone;
    }

    @Getter
    @AllArgsConstructor
    public static class Patch {
        private long memberId;

        private String password;

        @NotSpace(message = "회원 이름은 공백이 아니어야 합니다")
        private String name;

        @NotSpace(message = "휴대폰 번호는 공백이 아니어야 합니다")
        @Pattern(regexp = "^010-\\d{3,4}-\\d{4}$",
                message = "휴대폰 번호는 010으로 시작하는 11자리 숫자와 '-'로 구성되어야 합니다")
        private String phone;

        private UserStatus status;


        public void setMemberId(long memberId) {
            this.memberId = memberId;
        }
    }

    @Getter @Setter
    @Builder
    @AllArgsConstructor
    public static class Response {
        private long memberId;
        private String email;
        private String name;
        private String phone;
        private UserStatus status;
        private int stampCount;
        private List<String> roles;

        public static Response of(Member member) {
            return Response.builder()
                    .memberId(member.getMemberId())
                    .email(member.getEmail())
                    .name(member.getName())
                    .phone(member.getPhone())
                    .status(member.getStatus())
                    .stampCount(member.getStamp().getStampCount())
                    .roles(member.getRoles())
                    .build();
        }
    }
}
