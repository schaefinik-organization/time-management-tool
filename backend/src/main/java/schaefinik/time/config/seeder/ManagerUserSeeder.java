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
@Order(3)
public class ManagerUserSeeder implements CommandLineRunner {
	private final TimeUserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${seed.manager.email:manager@example.com}")
	private String managerEmail;

	@Value("${seed.manager.password:Manager123!}")
	private String managerPassword;

	@Override
	public void run(String... args) {
		if (!userRepository.existsByUsername("manager")) {
			userRepository.save(TimeUserModel.builder()
					.username("manager")
					.email(managerEmail)
					.passwordHash(passwordEncoder.encode(managerPassword))
					.role(Role.ROLE_MANAGER)
					.enabled(true)
					.build());
		}
	}
}
