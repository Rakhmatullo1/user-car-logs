package com.rakhmatullo.usercarlogs.config;

import com.rakhmatullo.usercarlogs.entity.Position;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ApplicationConfig config;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth

                                .requestMatchers("/auth/register", "/auth/authenticate", "/swagger-ui/**", "/v3/api-docs").permitAll()
                                .requestMatchers("/auth/reset-password", "/auth", "/auth/my-details").hasAnyAuthority(Position.BOSS.name(), Position.ACCOUNTANT.name(), Position.DRIVER.name())
                                .requestMatchers("/companies/**").hasAuthority(Position.BOSS.name())
                                .requestMatchers("/cars").hasAnyAuthority(Position.BOSS.name(), Position.ACCOUNTANT.name(), Position.DRIVER.name())
                                .requestMatchers("/cars/**").hasAnyAuthority(Position.BOSS.name(), Position.ACCOUNTANT.name())
                                .requestMatchers("/permissions/send-permission", "/permissions/my-permissions", "/permissions/cancel/**", "/permissions/{id}").hasAnyAuthority(Position.BOSS.name(), Position.ACCOUNTANT.name(), Position.DRIVER.name())
                                .requestMatchers("/permissions", "/permissions/permit-permission/**", "/permissions/permit-permission/**").hasAnyAuthority(Position.BOSS.name(), Position.ACCOUNTANT.name())
                                .anyRequest()
                                .authenticated()
                )
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(config.authenticationProvider())
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
