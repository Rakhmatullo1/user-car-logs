package com.rakhmatullo.usercarlogs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Permission  implements Comparable<Permission>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    private Instant createdDate;
    private Instant startDate;
    private Instant endDate;
    private String startLocation;
    private String finishLocation;
    @ManyToOne
    private Car car;
    private String reason;
    private boolean isPermitted=false;
    private boolean isCanceled = false;

    @Override
    public int compareTo(Permission o) {
        return o.startDate.compareTo(this.startDate);
    }
}
