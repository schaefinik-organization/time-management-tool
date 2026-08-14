package schaefinik.time.controller.api.v1.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import schaefinik.time.service.ExportService;

@RestController
@RequestMapping("/api/v1/user/export")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole( 'ROLE_USER','ROLE_MANAGER', 'ROLE_ADMIN')")
public class UserExportController {

	private final ExportService exportService;

}
