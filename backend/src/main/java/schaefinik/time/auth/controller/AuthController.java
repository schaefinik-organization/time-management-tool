package schaefinik.time.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import schaefinik.time.auth.requestData.LoginRequest;
import schaefinik.time.auth.responseData.AuthResponse;
import schaefinik.time.auth.responseData.CurrentUserResponse;
import schaefinik.time.auth.service.AuthService;
import schaefinik.time.security.principal.TimeUserPrincipal;
import schaefinik.time.user.service.UserService;

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
    public CurrentUserResponse me(@AuthenticationPrincipal TimeUserPrincipal principal) {
        return userService.getCurrentUser(principal);
    }
}
