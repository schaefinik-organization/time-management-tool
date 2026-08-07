package schaefinik.time.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {

	@Builder.Default
	private LocalDateTime timestamp = LocalDateTime.now(Clock.systemDefaultZone());

	private int status;
	private String error;
	private String message;
	private String path;

	private Map<String, String> validationErrors;
}
