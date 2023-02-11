package com.qna.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Positive;

public class OrderCoffeeDto {

    @Getter
    public static class Request {
        @Positive
        private long coffeeId;

        @Positive
        private int quantity;
    }

    @Builder
    @Getter
    public static class Response {
        private long coffeeId;
        private int quantity;
        private String korName;
        private String engName;
        private int price;
    }
}
