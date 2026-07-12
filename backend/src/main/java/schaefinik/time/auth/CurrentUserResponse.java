package schaefinik.time.auth;

public record CurrentUserResponse(
    Long id,
    String username,
    String role) {
}
