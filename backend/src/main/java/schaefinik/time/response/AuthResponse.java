package schaefinik.time.response;

public record AuthResponse(
		String token,
		String tokenType,
		long expiresIn,
		String username,
		String role) {
}
