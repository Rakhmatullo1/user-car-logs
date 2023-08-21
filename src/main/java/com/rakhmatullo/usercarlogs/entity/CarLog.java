package com.rakhmatullo.usercarlogs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;


@Entity
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class CarLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Instant createdDate;
    @Enumerated(value = EnumType.STRING)
    private CarLogAction action;

    @ManyToOne
    @JoinColumn(name="car_id")
    private Car car;

    @ManyToOne
    @JoinColumn(name="permission_id")
    private Permission permission;
}
