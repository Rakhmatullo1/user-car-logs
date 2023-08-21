package com.rakhmatullo.usercarlogs.service;

import com.rakhmatullo.usercarlogs.entity.Company;
import com.rakhmatullo.usercarlogs.entity.Position;
import com.rakhmatullo.usercarlogs.entity.User;
import com.rakhmatullo.usercarlogs.entity.UserLogAction;
import com.rakhmatullo.usercarlogs.exception.AlreadyExistsException;
import com.rakhmatullo.usercarlogs.exception.CompanyNotFoundException;
import com.rakhmatullo.usercarlogs.exception.PasswordDoesNotMatchException;
import com.rakhmatullo.usercarlogs.exception.ProvideCompanyDetailsException;
import com.rakhmatullo.usercarlogs.repository.CompanyRepository;
import com.rakhmatullo.usercarlogs.repository.UserRepository;
import com.rakhmatullo.usercarlogs.service.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserLogService userLogService;
    private final CompanyRepository companyRepository;

    public AuthorizationResponse register(RegistrationBody body) throws AlreadyExistsException, ProvideCompanyDetailsException, CompanyNotFoundException {
        if(body.getCompanyName()==null){
            throw new ProvideCompanyDetailsException("Company name not provided");
        }
        Company company =null;
        if(!body.getPosition().equals(Position.BOSS)){
             company = companyRepository.findByName(body.getCompanyName()).orElseThrow(()->new CompanyNotFoundException("Company Not found"));
        }
        Optional<User> theUser = userRepository.findByUsername(body.getUserName());

        if(theUser.isPresent()) {
            throw new AlreadyExistsException("The User already registered");
        }
        User user = User.builder()
                .id(null)
                .firstName(body.getFirstName())
                .lastName(body.getLastName())
                .username(body.getUserName())
                .passportNumber(body.getPassportNumber())
                .passportSeria(body.getPassportSeria())
                .password(passwordEncoder.encode(body.getPassword()))
                .phoneNumber(body.getPhoneNumber())
                .position(body.getPosition())
                .company(company)
                .build();
        userRepository.save(user);
        userLogService.save(user, UserLogAction.REGISTERED);
        var token = jwtService.generateToken(user);
        return new AuthorizationResponse(token);
    }

    public AuthorizationResponse authenticate(AuthenticationRequest body) {
        Optional<User> user = userRepository.findByUsername(body.getUsername());
        if(user.isEmpty()) {
            throw  new UsernameNotFoundException(String.format("The user %s not found", body.getUsername()));
        }

        String thePassword = user.get().getPassword();
        if(!passwordEncoder.matches(body.getPassword(), thePassword)) {
            throw new UsernameNotFoundException(String.format("The user %s not found", body.getUsername()));
        }
        userLogService.save(user.get(), UserLogAction.LOGGED_IN);
        String token = jwtService.generateToken(user.get());
        return new AuthorizationResponse(token);
    }

    public String resetPassword(String password, String newPassword) throws PasswordDoesNotMatchException {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isEmpty()){
            throw new UsernameNotFoundException("Not found the user");
        }
        User theUser = user.get();
        String thePassword = theUser.getPassword();
        if(!passwordEncoder.matches(password, thePassword)){
            throw new PasswordDoesNotMatchException("password is wrong");
        }
        theUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(theUser);
        userLogService.save(theUser, UserLogAction.RESET_PASSWORD);
        return "Your password is changed successfully";
    }

    public String update(RegistrationBody userDto) throws AlreadyExistsException {
        if(userDto.getPassword()!=null) {
            throw new RuntimeException("You cannot change password");
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(username);
        System.out.println(userDto.getUserName());
        if(!userDto.getUserName().equals(username)){
            Optional<User> user = userRepository.findByUsername(userDto.getUserName());
            if(user.isPresent()) {
                throw new AlreadyExistsException("User already exists");
            }
        }

        User theUser = userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("You should authenticate"));

        User user1 = User
                .builder()
                .id(theUser.getId())
                .username(userDto.getUserName())
                .lastName(userDto.getLastName())
                .firstName(userDto.getFirstName())
                .passportNumber(userDto.getPassportNumber())
                .passportSeria(userDto.getPassportSeria())
                .position(userDto.getPosition())
                .company(  theUser.getCompany())
                .password(theUser.getPassword())
                .phoneNumber(userDto.getPhoneNumber())
                .build();
        userLogService.save(user1, UserLogAction.UPDATED);
        userRepository.save(user1);
        return "Successfully updated";
    }

    public RegistrationBody getData() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(userName).orElseThrow(()-> new UsernameNotFoundException("The user is not found"));
        return RegistrationBody
                .builder()
                .companyName(user.getCompany().getName())
                .userName(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .passportSeria(user.getPassportSeria())
                .passportNumber(user.getPassportNumber())
                .phoneNumber(user.getPhoneNumber())
                .position(user.getPosition())
                .password("******")
                .build();
    }
}
