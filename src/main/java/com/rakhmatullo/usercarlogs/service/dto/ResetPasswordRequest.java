package com.rakhmatullo.usercarlogs.service.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String password;
    private String newPassword;
}
