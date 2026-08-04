package schaefinik.time.config;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
public class ApiError {
	private LocalDateTime timestamp;
	private int status;
	private String error;
	private String message;
	private String path;
	// Für Validierungsfehler (z.B. Feld 'name' darf nicht leer sein)
	private Map<String, String> validationErrors;
}