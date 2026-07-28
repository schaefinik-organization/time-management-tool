package schaefinik.time.security.util;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import schaefinik.time.auth.service.AuthService;
import schaefinik.time.security.principal.TimeUserPrincipal;

@ExtendWith(MockitoExtension.class)
public class SecurityUtilTest {

  @InjectMocks
  private SecurityUtil sut;

}
