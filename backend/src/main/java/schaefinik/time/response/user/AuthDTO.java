package schaefinik.time.response.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthDTO {
	private String token;
	private String tokenType;
	private long expiresIn;
	private String username;
	private String role;

	public AuthDTO(String token, String tokenType, long expiresIn, String username, String role) {
		this.token = token;
		this.tokenType = tokenType;
		this.expiresIn = expiresIn;
		this.username = username;
		this.role = role;
	}
}
