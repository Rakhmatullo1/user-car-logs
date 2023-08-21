package com.rakhmatullo.usercarlogs.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PermissionDto {
    private Long id;
    private String car;

    private Instant startDate;
    private Instant endDate;
    private String startLocation;
    private String finishLocation;
    private String reason;
    private String permissionOwner;
    private boolean isPermitted;
    private PermissionStatus status;
}
