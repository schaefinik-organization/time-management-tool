package schaefinik.time.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import schaefinik.time.security.principal.TimeUserPrincipal;
import schaefinik.time.user.requestData.AccountRequest;
import schaefinik.time.user.requestData.UserRequest;
import schaefinik.time.user.service.UserService;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PutMapping(value = "/update-profile", produces = "application/json")
	public void updateProfile(
			@AuthenticationPrincipal TimeUserPrincipal principal,
			@Valid @RequestBody UserRequest request) {
		userService.updateUser(principal, request);
	}

	@PostMapping(value = "/change-password", produces = "application/json")
	public void changePassword(
			@AuthenticationPrincipal TimeUserPrincipal principal,
			@Valid @RequestBody AccountRequest request) {
		userService.changePassword(principal, request);
	}
}
