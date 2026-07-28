package schaefinik.time.auth.service;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import schaefinik.time.auth.requestData.LoginRequest;
import schaefinik.time.auth.responseData.AuthResponse;
import schaefinik.time.security.properties.JwtProperties;
import schaefinik.time.security.service.JwtService;
import schaefinik.time.user.enums.Role;
import schaefinik.time.user.model.TimeUser;
import schaefinik.time.user.repository.TimeUserRepository;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest implements WithAssertions {

    @InjectMocks
    private AuthService sut;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private TimeUserRepository userRepository;

    @Mock
    private LoginRequest loginRequest;

    @Test
    public void test_login_throws_badCredentialsException() {
        Mockito.when(loginRequest.username()).thenReturn("user");
        Mockito.when(loginRequest.password()).thenReturn("wrong-password");

        Mockito.when(authenticationManager.authenticate(
                        ArgumentMatchers.any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> sut.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    public void test_login_throws_badCredentialsException_when_user_not_found() {
        Mockito.when(loginRequest.username()).thenReturn("nonexistent");
        Mockito.when(loginRequest.password()).thenReturn("any");

        Mockito.when(authenticationManager.authenticate(
                        ArgumentMatchers.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);

        Mockito.when(userRepository.findByUsername("nonexistent")).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> sut.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    public void test_login_throws_disabledException_when_account_disabled() {
        Mockito.when(loginRequest.username()).thenReturn("disabledUser");
        Mockito.when(loginRequest.password()).thenReturn("any");

        Mockito.when(authenticationManager.authenticate(
                        ArgumentMatchers.any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new DisabledException("Account disabled"));

        assertThatThrownBy(() -> sut.login(loginRequest))
                .isInstanceOf(DisabledException.class);
    }

    @Test
    public void test_login_success_returns_authResponse() {
        Mockito.when(loginRequest.username()).thenReturn("goodUser");
        Mockito.when(loginRequest.password()).thenReturn("correct-password");

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authenticationManager.authenticate(
                        ArgumentMatchers.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        TimeUser user = TimeUser.builder()
                .id(1L)
                .username("goodUser")
                .email("good@example.com")
                .passwordHash("hash")
                .role(Role.ROLE_USER)
                .enabled(true)
                .build();

        Mockito.when(userRepository.findByUsername("goodUser")).thenReturn(java.util.Optional.of(user));
        Mockito.when(jwtService.generateToken(ArgumentMatchers.any())).thenReturn("token-123");
        Mockito.when(jwtProperties.getExpirationMs()).thenReturn(3600000L);

        AuthResponse resp = sut.login(loginRequest);

        assertThat(resp).isNotNull();
        assertThat(resp.token()).isEqualTo("token-123");
        assertThat(resp.username()).isEqualTo("goodUser");
        assertThat(resp.role()).isEqualTo(user.getRole().name());
    }
}
