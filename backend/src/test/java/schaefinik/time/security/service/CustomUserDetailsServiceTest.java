package schaefinik.time.security.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import schaefinik.time.auth.service.AuthService;
import schaefinik.time.security.principal.TimeUserPrincipal;
import schaefinik.time.user.repository.TimeUserRepository;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

  @InjectMocks
  private CustomUserDetailsService sut;

  @Mock
  private TimeUserRepository userRepository;

}
