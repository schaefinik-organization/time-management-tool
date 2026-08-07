package schaefinik.time.controller.api.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import schaefinik.time.request.LoginRequest;
import schaefinik.time.response.AuthResponse;
import schaefinik.time.response.UserResponse;
import schaefinik.time.service.AuthService;
import schaefinik.time.service.UserService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final UserService userService;

	@PostMapping(value = "/login", produces = "application/json")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}

	@GetMapping(value = "/me", produces = "application/json")
	public ResponseEntity<UserResponse> me() {
		return ResponseEntity.ok(userService.getCurrentUserData());
	}
}
