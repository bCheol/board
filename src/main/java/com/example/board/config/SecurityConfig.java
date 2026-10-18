package com.example.board.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/admin/setting/stop/**"))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/members/**", "/admin/setting/**").hasRole("UPPER_ADMIN")
                        .requestMatchers("/admin/reports/**", "/admin/scheduler/**").hasAnyRole("LOWER_ADMIN", "UPPER_ADMIN")
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/account/login")
                        .loginProcessingUrl("/account/login")
                        .defaultSuccessUrl("/", true)
                        .failureHandler((request, response, exception) -> {
                            String errorReason = null;
                            if (exception instanceof BadCredentialsException || exception instanceof DisabledException) {
                                errorReason = "badCredentials";
                            } else if (exception instanceof LockedException) {
                                errorReason = "block";
                            }
                            request.getSession().setAttribute("errorReason", errorReason);
                            response.sendRedirect("/account/login");
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/account/logout")
                        .logoutSuccessUrl("/")
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/access-denied")
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}