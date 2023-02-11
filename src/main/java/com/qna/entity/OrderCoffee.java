package com.qna.entity;

import com.qna.utils.Auditable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class OrderCoffee extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderCoffeeId;

    @Column(nullable = false)
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "ORDER_ID")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "COFFEE_ID")
    private Coffee coffee;

    public void setOrder(Order order) {
        this.order = order;
        if (!this.order.getOrderCoffees().contains(this)) {
            this.order.getOrderCoffees().add(this);
        }
    }

    public void setCoffee(Coffee coffee) {
        this.coffee = coffee;
        if (!this.coffee.getOrderCoffees().contains(this)) {
            this.coffee.setOrderCoffee(this);
        }
    }
}
