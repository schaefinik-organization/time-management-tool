package schaefinik.time.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import schaefinik.time.model.TimeUserModel;
import schaefinik.time.repository.TimeUserRepository;
import schaefinik.time.request.LoginRequest;
import schaefinik.time.response.AuthResponse;
import schaefinik.time.security.principal.TimeUserPrincipal;
import schaefinik.time.security.properties.JwtProperties;
import schaefinik.time.security.service.JwtService;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final JwtProperties jwtProperties;
	private final TimeUserRepository userRepository;

	public AuthResponse login(LoginRequest request) {
		try {
			// Spring Security prüft hier Passwort UND ob 'enabled' true ist
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.username(), request.password()));

			TimeUserModel user = userRepository.findByUsername(request.username())
					.orElseThrow(() -> new BadCredentialsException("Benutzer nicht gefunden"));

			TimeUserPrincipal principal = new TimeUserPrincipal(user);
			String token = jwtService.generateToken(principal);

			return new AuthResponse(
					token,
					"Bearer",
					jwtProperties.getExpirationMs(),
					user.getUsername(),
					user.getRole().name());

		} catch (BadCredentialsException e) {
			throw new BadCredentialsException("Ungültiger Benutzername oder Passwort");
		} catch (DisabledException e) {
			throw new DisabledException("Ihr Account wurde deaktiviert. Bitte kontaktieren Sie einen Admin.");
		}
	}
}