package com.qna.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.Entity;

@Embeddable
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Money {
    private Integer value;
}
