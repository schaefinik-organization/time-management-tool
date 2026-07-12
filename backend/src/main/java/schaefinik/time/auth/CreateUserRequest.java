package schaefinik.time.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import schaefinik.time.user.Role;

public record CreateUserRequest(
    @NotBlank @Size(min = 3, max = 120) String username,
    @NotBlank @Email @Size(max = 190) String email,
    @NotBlank @Size(min = 8, max = 100) String password,
    Role role) {
}
