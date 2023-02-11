package com.qna.mapper;

import com.qna.dto.OrderCoffeeDto;
import com.qna.dto.OrderDto;
import com.qna.entity.Coffee;
import com.qna.entity.Member;
import com.qna.entity.Order;
import com.qna.entity.OrderCoffee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    // 자동 매핑을 할 경우, Order가 OrderCoffee와 연관 관계 매핑을 할 방법이 없으므로 수동 매핑으로 해줘야 됨
    default Order orderPostDtoToOrder(OrderDto.OrderPostDto orderPostDto) {
        Order order = new Order();
        Member member = new Member();
        member.setMemberId(orderPostDto.getMemberId());

        List<OrderCoffee> orderCoffees = orderPostDto.getOrderCoffees().stream()
                .map(orderCoffeeDto -> {
                    OrderCoffee orderCoffee = new OrderCoffee();
                    Coffee coffee = new Coffee();
                    coffee.setCoffeeId(orderCoffeeDto.getCoffeeId());
                    orderCoffee.setOrder(order);
                    orderCoffee.setCoffee(coffee);
                    orderCoffee.setQuantity(orderCoffeeDto.getQuantity());
                    return orderCoffee;
                }).collect(Collectors.toList());
        order.setMember(member);
        order.setOrderCoffees(orderCoffees);

        return order;
    }

    Order orderPatchDtoToOrder(OrderDto.OrderPatchDto orderPatchDto);

    List<OrderDto.OrderResponseDto> ordersToOrderResponseDtos(List<Order> orders);


    @Mapping(source = "member.memberId", target = "memberId")
    OrderDto.OrderResponseDto orderToOrderResponseDto(Order order);


    @Mapping(source = "coffee.coffeeId", target = "coffeeId")
    @Mapping(source = "coffee.korName", target = "korName")
    @Mapping(source = "coffee.engName", target = "engName")
    @Mapping(source = "coffee.price.value", target = "price")
    OrderCoffeeDto.Response orderCoffeeToOrderCoffeeResponseDto(OrderCoffee orderCoffee);


    // 주문한 커피 정보를 수동으로 직접 매핑하는 예
    /**
     default OrderResponseDto orderToOrderResponseDto(Order order){
     // 객체 그래프 탐색을 통해 주문한 커피 정보를 가져온다.
     // N + 1 문제가 발생할 수 있다.
     List<OrderCoffee> orderCoffees = order.getOrderCoffees();

     OrderResponseDto orderResponseDto = new OrderResponseDto();
     orderResponseDto.setOrderId(order.getOrderId());
     orderResponseDto.setMemberId(order.getMember().getMemberId());
     orderResponseDto.setOrderStatus(order.getOrderStatus());
     orderResponseDto.setCreatedAt(order.getCreatedAt());

     // 주문한 커피 정보를 List<OrderCoffeeResponseDto>로 변경한다.
     orderResponseDto.setOrderCoffees(orderCoffeesToOrderCoffeeResponseDtos(orderCoffees));

     return orderResponseDto;
     }
     */

    // 수동으로 직접 매핑: 전체 수동 매핑
    /**
     default List<OrderCoffeeResponseDto> orderCoffeesToOrderCoffeeResponseDtos(
     List<OrderCoffee> orderCoffees) {
     return orderCoffees
     .stream()
     .map(orderCoffee -> {
     // 주문 등록 시에는 price 값이 null이므로, null 여부를 체크해야 한다.
     // TODO 주문 등록을 URI만 던져주는 방식으로 바꾸면 이 코드는 필요없어진다.
     Money coffeePrice = orderCoffee.getCoffee().getPrice();
     Integer price = coffeePrice != null ? coffeePrice.getValue() : null;

     return OrderCoffeeResponseDto
     .builder()
     .coffeeId(orderCoffee.getCoffee().getCoffeeId())
     .quantity(orderCoffee.getQuantity())
     .price(price)
     .korName(orderCoffee.getCoffee().getKorName())
     .engName(orderCoffee.getCoffee().getEngName())
     .build();
     })
     .collect(Collectors.toList());
     }
     */

    // 수동으로 직접 매핑: orderCoffeeToOrderCoffeeResponseDto(orderCoffee) 이용
    /**
     default List<OrderCoffeeResponseDto> orderCoffeesToOrderCoffeeResponseDtos(List<OrderCoffee> orderCoffees) {
     return orderCoffees
     .stream()
     .map(orderCoffee -> orderCoffeeToOrderCoffeeResponseDto(orderCoffee))
     .collect(Collectors.toList());
     }
     */
}
