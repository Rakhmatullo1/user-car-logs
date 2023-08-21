package com.rakhmatullo.usercarlogs.repository;

import com.rakhmatullo.usercarlogs.entity.PermissionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionLogRepository extends JpaRepository<PermissionLog, Long> {
    List<PermissionLog> getPermissionLogByPermission_Id(Long id);

}
