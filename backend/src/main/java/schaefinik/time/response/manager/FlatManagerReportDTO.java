package schaefinik.time.response.manager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlatManagerReportDTO {
	private Long userId;
	private String username;

	private Long projectId;
	private String projectName;

	private double totalHours;
	private double billableHours;
	private long entryCount;
}