package schaefinik.time.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class AssignUsersRequest {
	@NotNull(message = "Die Liste der User-IDs darf nicht null sein")
	private Set<Long> userIds;
}