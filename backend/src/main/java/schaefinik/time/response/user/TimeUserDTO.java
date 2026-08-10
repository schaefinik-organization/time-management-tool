package schaefinik.time.response.user;

import lombok.Getter;
import lombok.Setter;
import schaefinik.time.response.project.ProjectSummaryDTO;

import java.util.List;

@Getter
@Setter
public class TimeUserDTO {

	private Long id;
	private String username;
	private String email;
	private String role;
	private boolean enabled;
	// Nutze hier die Summary-DTOs um Endlosschleifen zu blockieren!
	private UserSummaryDTO manager;
	private List<UserSummaryDTO> subordinates;
	private List<ProjectSummaryDTO> assignedProjects;
}
