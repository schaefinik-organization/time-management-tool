package schaefinik.time.auth;

public record AuthResponse(
    String token,
    String tokenType,
    long expiresIn,
    String username,
    String role) {
}
