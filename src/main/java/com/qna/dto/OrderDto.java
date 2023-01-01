package com.qna.dto;


import com.qna.entity.Order;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {

    @Getter
    public class OrderPostDto {
        @Positive
        private long memberId;

        @NotNull
        @Valid
        private List<OrderCoffee.Request> orderCoffees;
    }

    @Getter
    public class OrderPatchDto {
        private long orderId;
        private Order.OrderStatus orderStatus;

        public void setOrderId(long orderId) {
            this.orderId = orderId;
        }
    }

    @Getter
    @Setter
    public class OrderResponseDto {
        private long orderId;
        private long memberId;
        private Order.OrderStatus orderStatus;
        private List<OrderCoffee.Response> orderCoffees;
        private LocalDateTime createdAt;

    }
}
