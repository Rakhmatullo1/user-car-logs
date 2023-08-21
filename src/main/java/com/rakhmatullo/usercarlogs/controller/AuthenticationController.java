package com.rakhmatullo.usercarlogs.controller;

import com.rakhmatullo.usercarlogs.exception.CompanyNotFoundException;
import com.rakhmatullo.usercarlogs.exception.ProvideCompanyDetailsException;
import com.rakhmatullo.usercarlogs.service.dto.*;
import com.rakhmatullo.usercarlogs.exception.AlreadyExistsException;
import com.rakhmatullo.usercarlogs.exception.PasswordDoesNotMatchException;
import com.rakhmatullo.usercarlogs.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public AuthorizationResponse register(@RequestBody RegistrationBody body) throws AlreadyExistsException, ProvideCompanyDetailsException, CompanyNotFoundException {
        return service.register(body);
    }


    @PostMapping("/authenticate")
    public AuthorizationResponse authenticate(@RequestBody AuthenticationRequest body) {
        return service.authenticate(body);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) throws PasswordDoesNotMatchException {
        return ResponseEntity.ok(service.resetPassword(request.getPassword(), request.getNewPassword()));
    }

    @PutMapping
    public ResponseEntity<String> update(@RequestBody RegistrationBody registrationBody) throws AlreadyExistsException {
        return ResponseEntity.ok(service.update(registrationBody));
    }

    @GetMapping("/my-details")
    public RegistrationBody getUser() {
        return service.getData();
    }

}
