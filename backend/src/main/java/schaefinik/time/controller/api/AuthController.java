package schaefinik.time.controller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import schaefinik.time.requestData.LoginRequest;
import schaefinik.time.responseData.AuthResponse;
import schaefinik.time.responseData.CurrentUserResponse;
import schaefinik.time.service.AuthService;
import schaefinik.time.service.UserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final UserService userService;

	@PostMapping(value = "/login", produces = "application/json")
	public AuthResponse login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request);
	}

	@GetMapping(value = "/me", produces = "application/json")
	public CurrentUserResponse me() {
		return userService.getCurrentUserData();
	}
}
