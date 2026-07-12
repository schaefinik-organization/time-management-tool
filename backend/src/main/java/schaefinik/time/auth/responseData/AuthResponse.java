package schaefinik.time.auth.responseData;
 
public record AuthResponse(
    String token,
    String tokenType,
    long expiresIn,
    String username,
    String role) {
}
