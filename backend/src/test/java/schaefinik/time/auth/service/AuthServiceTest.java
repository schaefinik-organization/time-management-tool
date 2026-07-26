package schaefinik.time.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import schaefinik.time.security.principal.TimeUserPrincipal;
import schaefinik.time.security.properties.JwtProperties;
import schaefinik.time.security.service.JwtService;
import schaefinik.time.auth.requestData.LoginRequest;
import schaefinik.time.auth.responseData.AuthResponse;
import schaefinik.time.user.model.TimeUser;
import schaefinik.time.user.repository.TimeUserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final TimeUserRepository userRepository;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        TimeUser user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        TimeUserPrincipal principal = new TimeUserPrincipal(user);
        String token = jwtService.generateToken(principal);

        return new AuthResponse(
                token,
                "Bearer",
                jwtProperties.getExpirationMs(),
                user.getUsername(),
                user.getRole().name());
    }
}
