package schaefinik.time.config.seeder;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import schaefinik.time.enums.Role;
import schaefinik.time.model.TimeUserModel;
import schaefinik.time.repository.TimeUserRepository;

@Component
@ConditionalOnProperty(name = "app.seeder.enabled", havingValue = "true", matchIfMissing = true)
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
			userRepository.save(TimeUserModel.builder()
					.username("admin")
					.email(adminEmail)
					.passwordHash(passwordEncoder.encode(adminPassword))
					.role(Role.ROLE_ADMIN)
					.enabled(true)
					.build());
		}
	}
}
