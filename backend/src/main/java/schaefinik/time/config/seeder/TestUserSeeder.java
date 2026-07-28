package schaefinik.time.config.seeder;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import schaefinik.time.user.enums.Role;
import schaefinik.time.user.model.TimeUser;
import schaefinik.time.user.repository.TimeUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Component
@ConditionalOnProperty(name = "app.seeder.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Order(3)
public class TestUserSeeder implements CommandLineRunner {
    private final TimeUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${seed.test.email:test@example.com}")
    private String testEmail;

    @Value("${seed.test.password:Test123!}")
    private String testPassword;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("test")) {
            userRepository.save(TimeUser.builder()
                .username("test")
                .email(testEmail)
                .passwordHash(passwordEncoder.encode(testPassword))
                .role(Role.ROLE_USER)
                .enabled(true)
                .build());
        }
    }
}
