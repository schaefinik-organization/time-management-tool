package schaefinik.time.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import schaefinik.time.auth.service.AuthService;
import schaefinik.time.security.principal.TimeUserPrincipal;
import schaefinik.time.security.properties.JwtProperties;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

  @InjectMocks
  private JwtService sut;

  @Mock
  private JwtProperties jwtProperties;

}
