package com.rakhmatullo.usercarlogs.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class PermissionLog implements Comparable<PermissionLog> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private Instant createdDate;

    @Enumerated(value = EnumType.STRING)
    private PermissionLogAction action;

    @ManyToOne
    @JoinColumn(name="permission_id")
    private Permission permission;

    @Override
    public int compareTo(PermissionLog o) {
        return (-1)*this.getCreatedDate().compareTo(o.createdDate);
    }
}
