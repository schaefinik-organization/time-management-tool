package schaefinik.time.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import schaefinik.time.auth.requestData.UserRequest;
import schaefinik.time.auth.responseData.UserResponse;
import schaefinik.time.auth.service.AuthService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

  private final AuthService authService;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public UserResponse createUser(@Valid @RequestBody UserRequest request) {
    return authService.createUser(request);
  }
}
