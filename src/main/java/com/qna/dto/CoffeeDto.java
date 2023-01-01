package com.qna.dto;

import com.qna.entity.Coffee;
import com.qna.utils.NotSpace;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class CoffeeDto {

    @Getter
    public static class CoffeePostDto {
        @NotBlank
        private String korName;

        @NotBlank
        @Pattern(regexp = "^([A-Za-z])(\\s?[A-Za-z])*$",
                message = "커피명(영문)은 영문이어야 합니다(단어 사이 공백 한 칸 포함). 예) Cafe Latte")
        private String engName;

        @Range(min = 100, max = 50000)
        private int price;

        @NotBlank
        @Pattern(regexp = "^([A-Za-z]){3}$",
                message = "커피 코드는 3자리 영문이어야 합니다.")
        private String coffeeCode;
    }

    @Getter
    public class CoffeePatchDto {
        private long coffeeId;

        @NotSpace(message = "커피명(한글)은 공백이 아니어야 합니다.")
        private String korName;

        @Pattern(regexp = "^([A-Za-z])(\\s?[A-Za-z])*$", message = "커피명(영문)은 영문이어야 합니다. 예) Cafe Latte")
        private String engName;

        @Range(min = 100, max = 50000)
        private Integer price;

        // 추가된 부분. 커피 상태 값을 사전에 체크하는 Custom Validator를 만들수도 있다.
        private Coffee.CoffeeStatus coffeeStatus;

        public void setCoffeeId(long coffeeId) {
            this.coffeeId = coffeeId;
        }

        public Integer getPrice() {
            return price;
        }
    }

    @Builder
    @Getter
    public static class CoffeeResponseDto {
        private long coffeeId;
        private String korName;
        private String engName;
        private int price;
        private Coffee.CoffeeStatus coffeeStatus; // 커피 상태 추가

        public String getCoffeeStatus() {
            return coffeeStatus.getStatus();
        }
    }
}
