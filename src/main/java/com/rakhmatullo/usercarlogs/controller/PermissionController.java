package com.rakhmatullo.usercarlogs.controller;


import com.rakhmatullo.usercarlogs.exception.CarNotFoundException;
import com.rakhmatullo.usercarlogs.exception.PermissionNotFoundException;
import com.rakhmatullo.usercarlogs.service.PermissionService;
import com.rakhmatullo.usercarlogs.service.dto.PermissionDto;
import com.rakhmatullo.usercarlogs.service.dto.PermissionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    @PostMapping("/send-permission")
    public ResponseEntity<String> sendPermission(@RequestBody PermissionRequest request) throws CarNotFoundException, PermissionNotFoundException {
        return ResponseEntity.ok(permissionService.sendPermission(request));
    }

    @GetMapping
    public List<PermissionDto> getAllPermissions() {
        return permissionService.getAllPermissions();
    }

    @GetMapping("/permit-permission/{id}")
    public PermissionDto permit( @PathVariable Integer id) throws PermissionNotFoundException, CarNotFoundException {
        return permissionService.permitPermission(id.longValue());
    }
    @GetMapping("/reject-permission/{id}")
    public PermissionDto reject( @PathVariable Integer id) throws PermissionNotFoundException, CarNotFoundException {
        return permissionService.rejectPermission(id.longValue());
    }

    @GetMapping("/my-permissions")
    public List<PermissionDto> checkMyPermission() {
        return  permissionService.checkMyPermission();
    }

    @GetMapping("/cancel/{id}")
    public PermissionDto cancel(@PathVariable Integer id) throws PermissionNotFoundException, CarNotFoundException {
        return permissionService.cancelMyPermission(id.longValue());
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Integer id, @RequestBody PermissionRequest request) throws Exception {
        return permissionService.update(id.longValue(), request);
    }


}
