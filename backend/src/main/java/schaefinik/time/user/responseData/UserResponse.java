package schaefinik.time.user.responseData;

public record UserResponse(
    Long id,
    String username,
    String email,
    String role,
    boolean enabled) {
}
