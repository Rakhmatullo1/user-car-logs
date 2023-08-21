package com.rakhmatullo.usercarlogs.service;


import com.rakhmatullo.usercarlogs.entity.Company;
import com.rakhmatullo.usercarlogs.entity.User;
import com.rakhmatullo.usercarlogs.exception.CompanyAlreadyExistsException;
import com.rakhmatullo.usercarlogs.repository.CompanyRepository;
import com.rakhmatullo.usercarlogs.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;

    private final UserRepository userRepository;


    public String addCompany(Company company) throws CompanyAlreadyExistsException {
        Optional<Company> theCompany = companyRepository.findByName(company.getName());
        if(theCompany.isPresent()){
            throw new CompanyAlreadyExistsException("Company Exists");
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("the user not found"));
        Company company1 = Company.builder()
                .id(null)
                .name(company.getName())
                .build();
        Company company2 = companyRepository.save(company1);
        user.setCompany(company2);
        userRepository.save(user);
        return "Company is added successfully";
    }

}
