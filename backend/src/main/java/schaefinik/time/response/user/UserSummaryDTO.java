package schaefinik.time.response.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSummaryDTO {
	private Long id;
	private String username;
	private String role;
}
