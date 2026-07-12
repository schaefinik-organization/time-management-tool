package schaefinik.time.project.requestData;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectRequest(
        @NotBlank(message = "Name ist erforderlich") @Size(max = 100, message = "Name darf maximal 100 Zeichen lang sein") String name,

        @Size(max = 1000, message = "Beschreibung darf maximal 1000 Zeichen lang sein") String description) {
}
