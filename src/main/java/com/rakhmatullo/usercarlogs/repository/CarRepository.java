package com.rakhmatullo.usercarlogs.repository;


import com.rakhmatullo.usercarlogs.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car,Long> {

    Optional<Car> findByNumber(String carNumber);

    List<Car> findByName(String carName);
}
