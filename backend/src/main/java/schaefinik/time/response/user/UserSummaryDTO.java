package schaefinik.time.response.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserSummaryDTO {
	private Long id;
	private String username;
	private String role;

	public UserSummaryDTO(Long id, String username) {
		this.id = id;
		this.username = username;
	}
}
