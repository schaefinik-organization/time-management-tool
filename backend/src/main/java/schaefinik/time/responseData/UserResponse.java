package schaefinik.time.responseData;

public record UserResponse(
		Long id,
		String username,
		String email,
		String role,
		boolean enabled) {
}
