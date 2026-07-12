package schaefinik.time.auth;

public record UserResponse(
    Long id,
    String username,
    String email,
    String role,
    boolean enabled) {
}
