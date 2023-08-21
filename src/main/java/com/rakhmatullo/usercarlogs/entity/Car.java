package com.rakhmatullo.usercarlogs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Car{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String number;
    private String color;
    private String technicalPassportNumber;

    private String name;
    @Enumerated(value = EnumType.STRING)
    private CarType carType;
    @Enumerated(value = EnumType.STRING)
    private CarStatus carStatus;

    @ManyToOne
    private Company company;
}
