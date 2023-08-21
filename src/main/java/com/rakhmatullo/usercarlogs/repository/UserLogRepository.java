package com.rakhmatullo.usercarlogs.repository;

import com.rakhmatullo.usercarlogs.entity.UserLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLogRepository extends JpaRepository<UserLog, Long> {
}
