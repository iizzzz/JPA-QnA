package com.qna.dto;


import com.qna.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderPostDto {
        @Positive
        private long memberId;

        @NotNull
        @Valid
        private List<OrderCoffeeDto.Request> orderCoffees;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderPatchDto {
        private long orderId;
        private Order.OrderStatus orderStatus;

        public void setOrderId(long orderId) {
            this.orderId = orderId;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderResponseDto {
        private long orderId;
        private long memberId;
        private Order.OrderStatus orderStatus;
        private List<OrderCoffeeDto.Response> orderCoffees;
        private LocalDateTime createdAt;

    }
}
