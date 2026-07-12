package schaefinik.time.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import schaefinik.time.user.TimeUserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final TimeUserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findByUsername(username)
        .map(TimeUserPrincipal::new)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
  }
}
