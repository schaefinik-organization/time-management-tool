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
public class AdminUserSeeder implements CommandLineRunner {

  private final TimeUserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) {
    if (userRepository.existsByUsername("admin")) {
      return;
    }

    TimeUser admin = TimeUser.builder()
        .username("admin")
        .email("admin@example.com")
        .passwordHash(passwordEncoder.encode("Admin123!"))
        .role(Role.ROLE_ADMIN)
        .enabled(true)
        .build();

    userRepository.save(admin);
  }
}
