package com.smartsched.security.config;

import com.smartsched.security.jwt.JwtAuthenticationEntryPoint;
import com.smartsched.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;




@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .cors(Customizer.withDefaults())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authenticationProvider(authenticationProvider)

                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(authenticationEntryPoint))

                // full authorizeHttpRequests block, for reference — only the "Shared" section changed:

                .authorizeHttpRequests(auth -> auth

                        // NOTE: /api/auth/** (login, change-password) no longer
                        // lives in this service — it moved to auth-service.
                        // Tokens issued by auth-service are still accepted here
                        // (see JwtAuthenticationFilter / JwtService, same jwt.secret).

                        // Principal
                        .requestMatchers("/api/admin/**")
                        .hasAnyRole("PRINCIPAL", "ADMIN")
                        .requestMatchers("/api/principal/**")
                        .hasRole("PRINCIPAL")

                        // HOD
                        .requestMatchers("/api/hod/**")
                        .hasRole("HOD")

                        // Faculty
                        .requestMatchers("/api/faculty/**")
                        .hasRole("FACULTY")

                        // Shared
                        .requestMatchers("/api/dashboard/**")
                        .hasAnyRole("PRINCIPAL", "ADMIN", "HOD", "FACULTY")   // <-- ADMIN added

                        .requestMatchers("/api/progress/**")
                        .hasAnyRole("PRINCIPAL", "HOD", "FACULTY")

                        // NOTE: LeaveController is mapped at "/api/leaves" (plural),
                        // not "/api/leave" — this matcher must match that exactly,
                        // otherwise it silently falls through to anyRequest().authenticated().
                        .requestMatchers("/api/leaves/**")
                        .hasAnyRole("PRINCIPAL", "ADMIN", "HOD", "FACULTY")

                        .requestMatchers("/api/lecture/**")
                        .hasAnyRole("PRINCIPAL", "HOD", "FACULTY")
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .anyRequest()
                        .authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}