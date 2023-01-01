package com.qna.dto.globalResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SingleResponseDto<T> {
    private T data;
}
