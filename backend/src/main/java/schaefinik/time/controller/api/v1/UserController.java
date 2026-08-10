package schaefinik.time.controller.api.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import schaefinik.time.request.AccountRequest;
import schaefinik.time.request.user.UserChangeRequest;
import schaefinik.time.response.user.TimeUserDTO;
import schaefinik.time.security.principal.TimeUserPrincipal;
import schaefinik.time.service.UserService;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping(value = "/current-user", produces = "application/json")
	public ResponseEntity<TimeUserDTO> getCurrentUser() {
		return ResponseEntity.ok(userService.getCurrentUserData());
	}

	@PutMapping(value = "/update-profile", produces = "application/json")
	public ResponseEntity<Boolean> updateProfile(
			@Valid @RequestBody UserChangeRequest request) {
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(userService.updateCurrentUser(request));
	}

	@PostMapping(value = "/change-password", produces = "application/json")
	public ResponseEntity<Void> changePassword(
			@AuthenticationPrincipal TimeUserPrincipal principal,
			@Valid @RequestBody AccountRequest request) {
		userService.changePassword(principal, request);
		return ResponseEntity.ok().build();
	}
}
