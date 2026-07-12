package schaefinik.time.auth.requestData;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
                @NotBlank String username,
                @NotBlank String password) {
}
