package schaefinik.time.response.manager;

import lombok.Data;
import schaefinik.time.response.user.UserSummaryDTO;

import java.util.ArrayList;
import java.util.List;

@Data
public class TeamMemberReportDTO {
	private UserSummaryDTO user;
	private double totalHoursOverall = 0.0;
	private double billableHoursOverall = 0.0;

	// Die Liste der Projekte, an denen dieser User gearbeitet hat
	private List<TeamMemberProjectSummaryDTO> projects = new ArrayList<>();

	public TeamMemberReportDTO(Long userId, String username) {
		this.user = new UserSummaryDTO(userId, username);
	}
}