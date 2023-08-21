package com.rakhmatullo.usercarlogs.service;

import com.rakhmatullo.usercarlogs.entity.*;
import com.rakhmatullo.usercarlogs.exception.CarNotFoundException;
import com.rakhmatullo.usercarlogs.exception.PermissionNotFoundException;
import com.rakhmatullo.usercarlogs.repository.*;
import com.rakhmatullo.usercarlogs.service.dto.PermissionDto;
import com.rakhmatullo.usercarlogs.service.dto.PermissionRequest;
import com.rakhmatullo.usercarlogs.service.dto.PermissionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final CarLogService carLogService;
    private final PermissionLogService permissionLogService;
    private final UserLogService userLogService;

    public String sendPermission(PermissionRequest permissionRequest) throws PermissionNotFoundException, CarNotFoundException {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        User theUser = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        findCar(permissionRequest, theUser, false, null, null);
        return "Your application is on work";
    }

    private void findCar(PermissionRequest permissionRequest, User theUser, boolean toUpdate, Long id, Car oldPermissionCar) throws PermissionNotFoundException, CarNotFoundException {
        if(oldPermissionCar!=null &&  oldPermissionCar.getName().trim().equals(permissionRequest.getCarName().trim())) {
            checkDate(permissionRequest, oldPermissionCar);
            carRepository.save(oldPermissionCar);
            fillRepos(theUser, permissionRequest,oldPermissionCar,toUpdate, id);
        } else {
            List<Car> cars = carRepository
                    .findByName(permissionRequest.getCarName())
                    .stream()
                    .filter(car1 -> car1.getCarStatus() == CarStatus.FREE)
                    .toList();
            if (cars.isEmpty()) {
                List<Car> theCars = carRepository
                        .findByName(permissionRequest.getCarName())
                        .stream().filter(car1 -> car1.getCarStatus() == CarStatus.ORDERED)
                        .toList();
                List<Permission> permissions = new ArrayList<>();
                theCars
                        .forEach(
                                car -> permissions.addAll(permissionRepository.getPermissionByCar_Id(car.getId()))
                        );
                if (permissions.isEmpty()) {
                    throw new PermissionNotFoundException("Permissions not found");
                }
                Permission thePermission = permissions
                        .stream()
                        .filter((permission -> permission.isPermitted() && !permission.isCanceled()))
                        .sorted()
                        .findFirst()
                        .orElseThrow(() -> new PermissionNotFoundException("Permitted Permissions not found"));
                if (thePermission.getStartDate().compareTo(permissionRequest.getEndDate()) > 0) {
                    fillRepos(theUser, permissionRequest, thePermission.getCar(), toUpdate, id);
                    if(permissionRequest.getStartDate().atZone(ZoneId.systemDefault()).getDayOfYear()==Instant.now().atZone(ZoneId.systemDefault()).getDayOfYear()){
                        thePermission.getCar().setCarStatus(CarStatus.NOT_FREE);
                        carRepository.save(thePermission.getCar());
                    }
                } else {
                    throw new CarNotFoundException(String.format("that type cars is not available now", permissionRequest.getCarName()));
                }
            } else {
                Car car = cars.stream().findFirst().orElseThrow(() -> new CarNotFoundException(String.format("that type cars is not available now", permissionRequest.getCarName())));
                fillRepos(theUser, permissionRequest, car, toUpdate, id);
                System.out.println(Instant.now().atZone(ZoneId.systemDefault()).getDayOfYear());
                System.out.println(permissionRequest.getStartDate().atZone(ZoneId.systemDefault()).getDayOfYear());
                checkDate(permissionRequest, car);
                carRepository.save(car);
            }
            if(toUpdate) {
                oldPermissionCar.setCarStatus(CarStatus.FREE);
                carRepository.save(oldPermissionCar);
                carLogService.save(oldPermissionCar, CarLogAction.RELEASED);
            }
        }




    }


    private void checkDate(PermissionRequest permissionRequest, Car car) throws CarNotFoundException {
        if(permissionRequest.getStartDate().atZone(ZoneId.systemDefault()).getDayOfYear()-Instant.now().atZone(ZoneId.systemDefault()).getDayOfYear()>1){
            car.setCarStatus(CarStatus.ORDERED);
            carLogService.save(car, CarLogAction.ORDERED);
        }
        if(permissionRequest.getStartDate().atZone(ZoneId.systemDefault()).getDayOfYear()-Instant.now().atZone(ZoneId.systemDefault()).getDayOfYear()<=1){
            car.setCarStatus(CarStatus.NOT_FREE);
        }

    }

    private void fillRepos(User theUser, PermissionRequest permissionRequest, Car car, boolean toUpdate, Long id) throws PermissionNotFoundException, CarNotFoundException {
        Permission permission = Permission
                .builder()
                .id(id)
                .car(car)
                .user(theUser)
                .startLocation(permissionRequest.getStartLocation())
                .finishLocation(permissionRequest.getFinishLocation())
                .reason(permissionRequest.getReason())
                .createdDate(Instant.now())
                .startDate(permissionRequest.getStartDate())
                .endDate(permissionRequest.getEndDate())
                .build();
        boolean carIdOrdered = carLogService.carIsOrdered(car);
        var permissionOne=  permissionRepository.save(permission);
        if(!carIdOrdered){
            carLogService.saveWithPermission(permissionOne, car, CarLogAction.ORDERED);
        }
        if(toUpdate){
            userLogService.save(theUser, UserLogAction.UPDATE_PERMISSION);
            permissionLogService.save(permissionOne, PermissionLogAction.UPDATED);
        } else {
            userLogService.save(theUser, UserLogAction.SEND_PERMISSION);
            permissionLogService.save(permissionOne, PermissionLogAction.REGISTERED);
        }


    }
    public List<PermissionDto> getAllPermissions() {
        List<Permission> permissions =  permissionRepository.findAll();
        return  getPermissions(permissions);
    }

    private PermissionDto rejectOrPermitPermission(Long id, boolean isPermitted) throws PermissionNotFoundException, CarNotFoundException {
        Permission permission = permissionRepository.findById(id).orElseThrow(()-> new PermissionNotFoundException("Permission not found"));
        permission.setPermitted(isPermitted);
        if(!isPermitted) {
            checkLogListSize(permission);
        }
        permissionLogService.save(permission, isPermitted  ? PermissionLogAction.ACCEPTED: PermissionLogAction.REJECTED);
        return  fillPermissionDto(permission, isPermitted ? PermissionStatus.ACCEPTED : PermissionStatus.REJECTED);
    }

    private void checkLogListSize(Permission permission) throws CarNotFoundException {
        Car car =permission.getCar();
        List<Permission> permissions = permissionRepository.getPermissionByCar_Id(car.getId());
        if(permissions.size()==1) {
            car.setCarStatus(CarStatus.FREE);
        }
        carRepository.save(car);
        carLogService.save(car, CarLogAction.RELEASED);
    }

    public PermissionDto permitPermission(Long id) throws PermissionNotFoundException, CarNotFoundException {
        return rejectOrPermitPermission(id, true);
    }

    public PermissionDto rejectPermission(Long id) throws PermissionNotFoundException, CarNotFoundException {
        return rejectOrPermitPermission(id, false);
    }


    private PermissionDto fillPermissionDto(Permission permission, PermissionStatus permissionStatus) {
       return PermissionDto
                .builder()
                .id(permission.getId())
                .car(permission.getCar().getName() + ", " + permission.getCar().getCarStatus().name() + ", " +permission.getCar().getNumber() )
                .permissionOwner(permission.getUser().getFirstName()+" " + permission.getUser().getLastName()+ ", " + permission.getUser().getUsername())
                .startLocation(permission.getStartLocation())
                .endDate(permission.getEndDate())
                .finishLocation(permission.getFinishLocation())
                .startDate(permission.getStartDate())
                .reason(permission.getReason())
                .isPermitted(permission.isPermitted())
                .status(permissionStatus)
                .build();
    }

    public List<PermissionDto> checkMyPermission() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(userName).orElseThrow(()-> new UsernameNotFoundException("the user not found"));
        List<Permission> permissions = permissionRepository.getPermissionByUser_Id(user.getId());
        return   getPermissions(permissions);
    }

    private List<PermissionDto> getPermissions(List<Permission> permissions) {
        return permissions.stream().map( permission -> {
            List<PermissionLog> permissionLogs = permissionLogService.getPermissions(permission);
            Optional<PermissionLog> log =permissionLogs.stream().sorted().findFirst();
            PermissionStatus status = null;
            if(log.isEmpty()){
                status = PermissionStatus.ON_PROCESS;
            } else if (log.get().getAction() == PermissionLogAction.REGISTERED) {
                status = PermissionStatus.ON_PROCESS;
            } else if (log.get().getAction() ==PermissionLogAction.ACCEPTED) {
                status = PermissionStatus.ACCEPTED;
            } else if(log.get().getAction() ==PermissionLogAction.REJECTED) {
                status = PermissionStatus.REJECTED;
            } else if(log.get().getAction() ==PermissionLogAction.UPDATED){
                status = PermissionStatus.ON_PROCESS;
                if(log.get().getPermission().isPermitted()){
                    status = PermissionStatus.ACCEPTED;
                }
                if(log.get().getPermission().isCanceled()){
                    status = PermissionStatus.CANCELED;
                }
            }
            else {
                status = PermissionStatus.CANCELED;
            }
            return fillPermissionDto(permission,status);
        }).toList();
    }

    public PermissionDto cancelMyPermission(Long id) throws PermissionNotFoundException, CarNotFoundException {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        Permission permission = permissionRepository.findById(id).orElseThrow(()->new PermissionNotFoundException("Permission not found"));
        if(!permission.getUser().getUsername().equals(userName)){
            throw new PermissionNotFoundException("Permission not found");
        }
        checkLogListSize(permission);
        permission.setCanceled(true);
        permissionRepository.save(permission);
        permissionLogService.save(permission, PermissionLogAction.CANCELED);
        return fillPermissionDto(permission, PermissionStatus.CANCELED);
    }


    public String update(Long id, PermissionRequest request ) throws Exception {
        Permission permission = permissionRepository.findById(id).orElseThrow(()-> new PermissionNotFoundException("Permission not found"));
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!permission.getUser().getUsername().equals(username)){
            throw new PermissionNotFoundException("Permission not found");
        }
        if(permission.isCanceled()){
            throw new Exception("your permission is canceled, send new one");
        }
        if(permission.isPermitted()){
            throw new Exception("your permission is not changeable");
        }
        User user =userRepository.findByUsername(username).orElseThrow();
        System.out.println(permission.getCar().getId());
        System.out.println(id);
        findCar(request, user, true, id, permission.getCar());
        return "Check your permissions";
    }

}
