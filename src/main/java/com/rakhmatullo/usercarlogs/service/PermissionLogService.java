package com.rakhmatullo.usercarlogs.service;

import com.rakhmatullo.usercarlogs.entity.Permission;
import com.rakhmatullo.usercarlogs.entity.PermissionLog;
import com.rakhmatullo.usercarlogs.entity.PermissionLogAction;
import com.rakhmatullo.usercarlogs.exception.PermissionNotFoundException;
import com.rakhmatullo.usercarlogs.repository.PermissionLogRepository;
import com.rakhmatullo.usercarlogs.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionLogService {

    private final PermissionRepository permissionRepository;
    private final PermissionLogRepository repository;
    public PermissionLog save(Permission permission, PermissionLogAction action) throws PermissionNotFoundException {
        Permission thePermission = permissionRepository
                .findById(permission.getId())
                .orElseThrow(()->new  PermissionNotFoundException("Permission Not Found"));
        PermissionLog permissionLog = PermissionLog
                .builder()
                .createdDate(Instant.now())
                .permission(thePermission)
                .action(action)
                .build();
        repository.save(permissionLog);
        return permissionLog;
    }

    public List<PermissionLog> getPermissions(Permission permission) {
        return repository
                .getPermissionLogByPermission_Id(permission.getId());
    }
}
