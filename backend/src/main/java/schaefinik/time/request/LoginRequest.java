package schaefinik.time.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
		@Size(max = 100) @NotBlank String username,
		@NotBlank String password) {
}
