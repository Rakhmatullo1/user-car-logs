package com.rakhmatullo.usercarlogs.repository;


import com.rakhmatullo.usercarlogs.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    List<Permission> getPermissionByCar_Id(Long carId);

    List<Permission> getPermissionByUser_Id(Long id);
}
