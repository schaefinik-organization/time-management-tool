package schaefinik.time.security.principal;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import schaefinik.time.user.model.TimeUser;

import java.util.Collection;
import java.util.List;

@Getter
public class TimeUserPrincipal implements UserDetails {

	private final Long id;
	private final String username;
	private final String email;
	private final String password;
	private final boolean enabled;
	private final List<SimpleGrantedAuthority> authorities;

	public TimeUserPrincipal(TimeUser user) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.email = user.getEmail();
		this.password = user.getPasswordHash();
		this.enabled = user.isEnabled();
		this.authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));
	}

	@Override
	public Collection<SimpleGrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
}
