package schaefinik.time.requestData;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import schaefinik.time.enums.Role;

public record UserRequest(
		@Size(min = 3, max = 120) String username,
		@Email @Size(max = 190) String email,
		@Size(min = 0, max = 100) String password,
		Boolean enabled,
		Role role) {
}
