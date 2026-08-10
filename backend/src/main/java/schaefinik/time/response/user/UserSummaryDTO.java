package schaefinik.time.response.user;

import lombok.Getter;
import lombok.Setter;
import schaefinik.time.model.TimeUserModel;

@Getter
@Setter
public class UserSummaryDTO {
	private Long id;
	private String username;
	private String role;

	public UserSummaryDTO(TimeUserModel model) {
		if (model != null) {
			this.id = model.getId();
			this.username = model.getUsername();
			this.role = model.getRole().name();
		}
	}
}
