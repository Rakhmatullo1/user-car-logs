package com.rakhmatullo.usercarlogs.controller;

import com.rakhmatullo.usercarlogs.exception.AlreadyExistsException;
import com.rakhmatullo.usercarlogs.exception.CarNotFoundException;
import com.rakhmatullo.usercarlogs.exception.CompanyNotFoundException;
import com.rakhmatullo.usercarlogs.exception.ProvideCompanyDetailsException;
import com.rakhmatullo.usercarlogs.service.CarService;
import com.rakhmatullo.usercarlogs.service.dto.CarDto;
import com.rakhmatullo.usercarlogs.service.dto.CarRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @PostMapping("/add-car")
    public ResponseEntity<String> addNewCar(@RequestBody CarRequest carRequest) throws ProvideCompanyDetailsException, CompanyNotFoundException, AlreadyExistsException, CarNotFoundException {
        carService.addCar(carRequest);
        return ResponseEntity.ok("Car is added successfully");
    }

    @GetMapping
    public List<CarDto> getAllCars() {
        return carService.getAllCars();
    }

    @PutMapping("/{id}")
    public CarDto update(@PathVariable Integer id, @RequestBody CarRequest carRequest) throws CarNotFoundException, CompanyNotFoundException {
        return carService.update(id.longValue(), carRequest);
    }
}
