package com.qna.mapper;

import com.qna.dto.CoffeeDto;
import com.qna.entity.Coffee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CoffeeMapper {

    @Mapping(source = "price", target = "price.value")
    Coffee coffeePostDtoToCoffee(CoffeeDto.CoffeePostDto coffeePostDto);

    @Mapping(source = "price", target = "price.value")
    Coffee coffeePatchDtoToCoffee(CoffeeDto.CoffeePatchDto coffeePatchDto);

    @Mapping(source = "price.value", target = "price")
    CoffeeDto.CoffeeResponseDto coffeeToCoffeeResponseDto(Coffee coffee);

    List<CoffeeDto.CoffeeResponseDto> coffeesToCoffeeResponseDtos(List<Coffee> coffees);
}
