package com.rakhmatullo.usercarlogs.repository;

import com.rakhmatullo.usercarlogs.entity.CarLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarLogRepository extends JpaRepository<CarLog, Long> {

    List<CarLog> getCarLogsByCar_Id(Long id);

}
