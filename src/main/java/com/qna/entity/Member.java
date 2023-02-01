package com.qna.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.qna.entity.contant.UserStatus;
import com.qna.utils.Auditable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Member extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 150)
    private String password;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 13, nullable = false, unique = true)
    private String phone;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 20, nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

    @OneToOne(mappedBy = "member", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Stamp stamp;

    // 사용자 등록 시, 사용자의 권한 추가
    // RDB는 컬렉션을 저장할수 없으므로 컬렉션 객체인걸 어노테이션으로 명시
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

    public Member(String email) {
        this.email = email;
    }

    public Member(String email, String name, String phone, String password) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.password = password;
    }

    public void setOrder(Order order) {
        orders.add(order);
        if (order.getMember() != this) {
            order.setMember(this);
        }
    }

    public void setStamp(Stamp stamp) {
        this.stamp = stamp;
        if (stamp.getMember() != this) {
            stamp.setMember(this);
        }
    }
}
