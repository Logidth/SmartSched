package com.smartsched.auth.config;

import com.smartsched.auth.entity.User;
import com.smartsched.auth.repository.UserRepository;
import com.smartsched.common.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultAdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (userRepository.existsByUsername("principal")) {
            return;
        }

        User principal = new User();

        principal.setUsername("principal");
        principal.setPassword(
                passwordEncoder.encode("admin123")
        );
        principal.setRole(Role.PRINCIPAL);
        principal.setActive(true);
        principal.setAccountLocked(false);
        principal.setFirstLogin(true);

        userRepository.save(principal);

        System.out.println("-----------------------------------");
        System.out.println("Default Principal Created");
        System.out.println("Username : principal");
        System.out.println("Password : admin123");
        System.out.println("-----------------------------------");
    }
}