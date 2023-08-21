package com.rakhmatullo.usercarlogs.service;

import com.rakhmatullo.usercarlogs.entity.Car;
import com.rakhmatullo.usercarlogs.entity.CarLog;
import com.rakhmatullo.usercarlogs.entity.CarLogAction;
import com.rakhmatullo.usercarlogs.entity.Permission;
import com.rakhmatullo.usercarlogs.exception.CarNotFoundException;
import com.rakhmatullo.usercarlogs.exception.PermissionNotFoundException;
import com.rakhmatullo.usercarlogs.repository.CarLogRepository;
import com.rakhmatullo.usercarlogs.repository.CarRepository;
import com.rakhmatullo.usercarlogs.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarLogService {

    private final CarLogRepository carLogRepository;
    private final PermissionRepository permissionRepository;
    private final CarRepository carRepository;

    public CarLog saveWithPermission(Permission permission , Car car, CarLogAction action) throws CarNotFoundException, PermissionNotFoundException {
        Car theCar = carRepository.findById(car.getId()).orElseThrow(()->new CarNotFoundException("Car not found"));
        Permission thePermission = permissionRepository.findById(permission.getId()).orElseThrow(()-> new PermissionNotFoundException("Permission Not Found"));
        CarLog carLog = CarLog
                .builder()
                .createdDate(Instant.now())
                .action(action)
                .car(theCar)
                .permission(thePermission)
                .build();
        return carLogRepository.save(carLog);
    }

    public CarLog save( Car car, CarLogAction action) throws CarNotFoundException {
        Car theCar = carRepository.findById(car.getId()).orElseThrow(()->new CarNotFoundException("Car not found"));
        CarLog carLog = CarLog
                .builder()
                .createdDate(Instant.now())
                .action(action)
                .car(theCar)
                .permission(null)
                .build();
        return carLogRepository.save(carLog);
    }

    public boolean carIsOrdered(Car car) {
        List<CarLog> carLogs = carLogRepository.getCarLogsByCar_Id(car.getId());

        if(carLogs.isEmpty()) {
            return  false;
        }
        List<CarLog> theCarLogs = carLogs.stream().filter((carLog -> carLog.getAction() == CarLogAction.ORDERED)).toList();
        if(!theCarLogs.isEmpty()){
            return  true;
        }
        return !true;
    }
}
