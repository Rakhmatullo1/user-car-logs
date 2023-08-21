package com.rakhmatullo.usercarlogs.service.dto;

import com.rakhmatullo.usercarlogs.entity.Position;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationBody {
    private String userName;
    private String lastName;
    private String firstName;
    private String passportSeria;
    private String passportNumber;
    private String phoneNumber;
    private String password;
    private Position position;
    private String companyName;
}
