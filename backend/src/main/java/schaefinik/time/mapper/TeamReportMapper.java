package schaefinik.time.mapper;

import org.springframework.stereotype.Component;
import schaefinik.time.response.manager.FlatManagerReportDTO;
import schaefinik.time.response.manager.TeamMemberProjectSummaryDTO;
import schaefinik.time.response.manager.TeamMemberReportDTO;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class TeamReportMapper {
	public List<TeamMemberReportDTO> mapToTeamMemberReports(List<FlatManagerReportDTO> flatData) {
		if (flatData == null || flatData.isEmpty()) {
			return new ArrayList<>();
		}

		Map<Long, TeamMemberReportDTO> userMap = new LinkedHashMap<>();

		for (FlatManagerReportDTO row : flatData) {
			TeamMemberReportDTO userReport = userMap.computeIfAbsent(
					row.getUserId(),
					id -> new TeamMemberReportDTO(id, row.getUsername())
			);

			userReport.setTotalHoursOverall(userReport.getTotalHoursOverall() + row.getTotalHours());
			userReport.setBillableHoursOverall(userReport.getBillableHoursOverall() + row.getBillableHours());

			TeamMemberProjectSummaryDTO projectSummary = new TeamMemberProjectSummaryDTO(
					row.getProjectId(),
					row.getProjectName(),
					row.getTotalHours(),
					row.getBillableHours(),
					row.getEntryCount()
			);
			userReport.getProjects().add(projectSummary);
		}

		return new ArrayList<>(userMap.values());
	}
}