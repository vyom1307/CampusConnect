package com.movement.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors()
                .and()
            .csrf().disable() // disable CSRF for testing with Postman
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/test/**", "/api/auth/verify","/complaints/**").permitAll() // allow test endpoint without login
                    .anyRequest().authenticated() // all other requests require auth
            )
            .httpBasic(); // you can change to formLogin() if needed

        return http.build();
//        http
//                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().permitAll()
//                );
//
//        return http.build();
    }
}
