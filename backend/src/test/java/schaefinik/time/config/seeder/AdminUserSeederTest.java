package schaefinik.time.config.seeder;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import schaefinik.time.auth.service.AuthService;
import schaefinik.time.user.enums.Role;
import schaefinik.time.user.model.TimeUser;
import schaefinik.time.user.repository.TimeUserRepository;

@ExtendWith(MockitoExtension.class)
public class AdminUserSeederTest{

  @InjectMocks
  private AdminUserSeeder sut;

  @Mock
  private TimeUserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

}
