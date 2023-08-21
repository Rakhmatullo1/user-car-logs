package com.rakhmatullo.usercarlogs.controller;

import com.rakhmatullo.usercarlogs.entity.Company;
import com.rakhmatullo.usercarlogs.exception.CompanyAlreadyExistsException;
import com.rakhmatullo.usercarlogs.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping("/add-new-company")
    public ResponseEntity<String> addCompany(@RequestBody  Company company) throws CompanyAlreadyExistsException {
        return ResponseEntity.ok(companyService.addCompany(company));
    }
}
