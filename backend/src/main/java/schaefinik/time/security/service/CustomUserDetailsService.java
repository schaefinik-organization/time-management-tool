package schaefinik.time.security.service;

import lombok.RequiredArgsConstructor;
import schaefinik.time.security.principal.TimeUserPrincipal;
import schaefinik.time.user.repository.TimeUserRepository;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

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
