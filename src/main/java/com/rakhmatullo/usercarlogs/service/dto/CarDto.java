package com.rakhmatullo.usercarlogs.service.dto;

import com.rakhmatullo.usercarlogs.entity.CarType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CarDto {
    private Long id;
    private String number;
    private String color;
    private String technicalPassportNumber;
    private String name;
    private CarType carType;
    private String companyName;
}
