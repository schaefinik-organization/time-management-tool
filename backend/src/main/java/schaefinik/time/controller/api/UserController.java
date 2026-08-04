package schaefinik.time.controller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import schaefinik.time.requestData.AccountRequest;
import schaefinik.time.requestData.UserRequest;
import schaefinik.time.responseData.UserResponse;
import schaefinik.time.security.principal.TimeUserPrincipal;
import schaefinik.time.service.UserService;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PutMapping(value = "/update-profile", produces = "application/json")
	public ResponseEntity<UserResponse> updateProfile(
			@Valid @RequestBody UserRequest request) {
		return ResponseEntity.ok(userService.updateCurrentUser(request));
	}

	@PostMapping(value = "/change-password", produces = "application/json")
	public ResponseEntity<Void> changePassword(
			@AuthenticationPrincipal TimeUserPrincipal principal,
			@Valid @RequestBody AccountRequest request) {
		userService.changePassword(principal, request);
		return ResponseEntity.ok().build();
	}
}
