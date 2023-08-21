package com.rakhmatullo.usercarlogs.service;

import com.rakhmatullo.usercarlogs.entity.User;
import com.rakhmatullo.usercarlogs.entity.UserLog;
import com.rakhmatullo.usercarlogs.entity.UserLogAction;
import com.rakhmatullo.usercarlogs.repository.UserLogRepository;
import com.rakhmatullo.usercarlogs.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserLogService {

    private final UserLogRepository userLogRepository;
    private final UserRepository userRepository;

    public UserLog save(User user,UserLogAction action ) {
        return  this.save(user.getId(), action);
    }

    public UserLog save(Long userId, UserLogAction action) {
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()) {
            throw new UsernameNotFoundException("Not Found username");
        }
        UserLog userLog = UserLog.builder()
                .createdDate( Instant.now())
                .action(action)
                .user(user.get())
                .build();
        userLogRepository.save(userLog);
        return  userLog;
    }
}
