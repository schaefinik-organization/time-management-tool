package schaefinik.time.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import schaefinik.time.enums.Role;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserChangeRequest {
	@NotBlank
	String username;
	@Email
	@Size(max = 190)
	String email;
	@Size(min = 0, max = 100)
	String password;
	Role role;
	Boolean enabled;
	List<Long> subordinateIds;
}
