package com.user.service.configuracion;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.user.service.Entidades.Role;
import com.user.service.Entidades.Status;
import com.user.service.Entidades.User;
import com.user.service.repositorio.UserRepository;

@Component
@Profile("dev")
public class DevAdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.admin.enabled:true}")
    private boolean enabled;

    @Value("${app.seed.admin.email:admin@local.com}")
    private String email;

    @Value("${app.seed.admin.username:admin}")
    private String username;

    @Value("${app.seed.admin.password:admin12345}")
    private String password;

    @Value("${app.seed.admin.displayName:Admin}")
    private String displayName;

    public DevAdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!enabled) return;

        boolean exists = userRepository.existsByEmailIgnoreCase(email)
                || userRepository.existsByUsernameIgnoreCase(username);

        if (exists) return;

        User admin = new User();
        admin.setEmail(email);
        admin.setUsername(username);
        admin.setDisplayName(displayName);
        admin.setPasswordHash(passwordEncoder.encode(password));
        admin.setRole(Role.ADMIN);
        admin.setStatus(Status.ACTIVE);

        userRepository.save(admin);

        System.out.println("[DEV] Admin creado: " + username + " / " + email);
    }
}