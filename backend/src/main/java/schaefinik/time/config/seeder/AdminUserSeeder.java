package schaefinik.time.config.seeder;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import schaefinik.time.user.enums.Role;
import schaefinik.time.user.model.TimeUser;
import schaefinik.time.user.repository.TimeUserRepository;

@Component
@RequiredArgsConstructor
@Order(1) // Zuerst ausführen
public class AdminUserSeeder implements CommandLineRunner {
    private final TimeUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${seed.admin.email:admin@example.com}")
    private String adminEmail;

    @Value("${seed.admin.password:Admin123!}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            userRepository.save(TimeUser.builder()
                .username("admin")
                .email(adminEmail)
                .passwordHash(passwordEncoder.encode(adminPassword))
                .role(Role.ROLE_ADMIN)
                .enabled(true)
                .build());
        }
    }
}
