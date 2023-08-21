package com.rakhmatullo.usercarlogs.service;


import com.rakhmatullo.usercarlogs.entity.*;
import com.rakhmatullo.usercarlogs.exception.AlreadyExistsException;
import com.rakhmatullo.usercarlogs.exception.CarNotFoundException;
import com.rakhmatullo.usercarlogs.exception.CompanyNotFoundException;
import com.rakhmatullo.usercarlogs.exception.ProvideCompanyDetailsException;
import com.rakhmatullo.usercarlogs.repository.CarRepository;
import com.rakhmatullo.usercarlogs.repository.CompanyRepository;
import com.rakhmatullo.usercarlogs.repository.UserRepository;
import com.rakhmatullo.usercarlogs.service.dto.CarDto;
import com.rakhmatullo.usercarlogs.service.dto.CarRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;
    private final CompanyRepository companyRepository;
    private final CarLogService carLogService;
    private final UserRepository userRepository;

    public void addCar(CarRequest carRequest) throws ProvideCompanyDetailsException, CompanyNotFoundException, AlreadyExistsException, CarNotFoundException {
        Optional<Car> theCar = carRepository.findByNumber(carRequest.getNumber());
        if(theCar.isPresent()) {
            throw new AlreadyExistsException("the car already exists");
        }
        if(carRequest.getCompanyName()==null) {
            throw new ProvideCompanyDetailsException("Company name is not provided");
        }
        Company company = companyRepository.findByName(carRequest.getCompanyName()).orElseThrow(()-> new CompanyNotFoundException("Company Not Found"));
        Car car = Car
                .builder()
                .number(carRequest.getNumber())
                .color(carRequest.getColor())
                .technicalPassportNumber(carRequest.getTechnicalPassportNumber())
                .name(carRequest.getName())
                .carType(carRequest.getCarType())
                .carStatus(CarStatus.FREE)
                .company(company)
                .build();
        carRepository.save(car);
        carLogService.save(car, CarLogAction.REGISTERED);
    }

    public List<CarDto> getAllCars() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("User not Found"));
        List<Car> cars = carRepository.findAll();
        return cars.stream()
                .map(this::fillCarDto).filter(carDto -> carDto.getCompanyName().equals(user.getCompany().getName())).toList();
    }

    public CarDto update(Long id, CarRequest carRequest) throws CarNotFoundException, CompanyNotFoundException {
        Car car =carRepository.findById(id).orElseThrow(()-> new CarNotFoundException("the car not found"));
        Company company = companyRepository.findByName(car.getCompany().getName()).orElseThrow(()-> new CompanyNotFoundException("Company Not Found"));
        car = Car
                .builder()
                .id(car.getId())
                .number(carRequest.getNumber())
                .color(carRequest.getColor())
                .technicalPassportNumber(carRequest.getTechnicalPassportNumber())
                .name(carRequest.getName())
                .carType(carRequest.getCarType())
                .carStatus(car.getCarStatus())
                .company(company)
                .build();
        carRepository.save(car);
        carLogService.save(car, CarLogAction.UPDATED);
        return fillCarDto(car);
    }
    private CarDto fillCarDto(Car car) {
       return CarDto
                .builder()
               .id(car.getId())
                .name(car.getName() + ", " + car.getCarStatus().name())
                .companyName(car.getCompany().getName())
                .number(car.getNumber())
                .technicalPassportNumber(car.getTechnicalPassportNumber())
                .color(car.getColor())
                .carType(car.getCarType())
                .build();
    }


}
