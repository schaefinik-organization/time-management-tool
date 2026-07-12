package schaefinik.time.security;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtil {

  private SecurityUtil() {
  }

  public static TimeUserPrincipal currentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !(authentication.getPrincipal() instanceof TimeUserPrincipal principal)) {
      throw new AuthenticationCredentialsNotFoundException("No authenticated user found");
    }
    return principal;
  }
}
