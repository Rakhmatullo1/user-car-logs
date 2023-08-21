package com.rakhmatullo.usercarlogs.service.dto;

import com.rakhmatullo.usercarlogs.entity.CarType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarRequest {
    private String number;
    private String color;
    private String technicalPassportNumber;
    private String name;
    private CarType carType;
    private String companyName;
}
