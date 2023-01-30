package com.qna.security.filter;

import com.google.gson.Gson;
import com.qna.error.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class MemberAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        log.error("# Authentication Failed: {}", exception.getMessage());

        // 메서드를 호출해 출력 스트림에 Error 정보를 담는다
        sendErrorResponse(response);
    }

    private void sendErrorResponse(HttpServletResponse response) throws IOException {
        // 에러정보가 담긴 객체(ErrorResponse)를 Json으로 변환하는 인스턴스
        Gson gson = new Gson();

        // ErrorResponse 객체를 생성하고 of 메서드로 상태코드 전달
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.UNAUTHORIZED);

        // 클라이언트에게 알려줄 수 있도록 타입을 헤더에 추가
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(gson.toJson(errorResponse, ErrorResponse.class));
    }
}
