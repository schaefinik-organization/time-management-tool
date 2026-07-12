package schaefinik.time.auth.responseData;

public record CurrentUserResponse(
    Long id,
    String username,
    String role) {
}
