package schaefinik.time.report.responseData;

public record ReportDTO(
        String username,
        String projectName,
        Long totalMinutes) {
}
