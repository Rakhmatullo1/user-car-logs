package com.rakhmatullo.usercarlogs.service.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class PermissionRequest {
    private Long id;
    private String carName;
    private Instant startDate;
    private Instant endDate;
    private String startLocation;
    private String finishLocation;
    private String reason;
}
