package schaefinik.time.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import schaefinik.time.enums.Role;

@Data
public class UserCreateRequest {
	@NotBlank
	String username;
	@Email
	@Size(max = 190)
	String email;
	@Size(min = 0, max = 100)
	String password;
	Role role;
	boolean enabled;
}
