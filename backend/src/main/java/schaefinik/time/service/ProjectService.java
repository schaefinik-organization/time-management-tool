package schaefinik.time.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import schaefinik.time.enums.CurrencyCode;
import schaefinik.time.enums.Role;
import schaefinik.time.exception.ResourceNotFoundException;
import schaefinik.time.model.ProjectModel;
import schaefinik.time.model.TimeUserModel;
import schaefinik.time.repository.ProjectRepository;
import schaefinik.time.request.ProjectRequest;
import schaefinik.time.response.ProjectResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

	private final ProjectRepository projectRepository;
	private final UserService userService;

	@Transactional(readOnly = true)
	public List<ProjectResponse> findAllAssignedToCurrentUser() {
		TimeUserModel currentUser = userService.getCurrentUser();
		return projectRepository.findByAssignedUsersContainingAndActiveIsTrue(currentUser)
				.stream()
				.map(this::mapToResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<ProjectResponse> findAllManagedByCurrentUser() {
		TimeUserModel currentUser = userService.getCurrentUser();
		return projectRepository.findByManager(currentUser)
				.stream()
				.map(this::mapToResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public ProjectResponse getProjectData(Long id) {
		return mapToResponse(getProject(id));
	}

	protected ProjectModel getProject(Long id) {
		return projectRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Projekt mit ID " + id + " nicht gefunden"));
	}

	@Transactional
	public ProjectResponse create(ProjectRequest request) {
		TimeUserModel currentUser = userService.getCurrentUser();

		ProjectModel project = ProjectModel.builder()
				.name(request.getName())
				.description(request.getDescription())
				.manager(currentUser)
				.active(true)
				.hourlyRate(request.getHourlyRate() != null ? request.getHourlyRate() : BigDecimal.ZERO)
				.currency(request.getCurrency() != null ? request.getCurrency() : CurrencyCode.EUR)
				.build();

		return mapToResponse(projectRepository.save(project));
	}

	@Transactional
	public ProjectResponse update(Long id, ProjectRequest request) {
		ProjectModel project = getProject(id);
		verifyManagerAuthority(project);

		project.setName(request.getName());
		project.setDescription(request.getDescription());

		if (request.getHourlyRate() != null) {
			project.setHourlyRate(request.getHourlyRate());
		}
		if (request.getCurrency() != null) {
			project.setCurrency(request.getCurrency());
		}

		return mapToResponse(projectRepository.save(project));
	}

	@Transactional
	public ProjectResponse assignUsers(Long projectId, Set<Long> userIds) {
		ProjectModel project = getProject(projectId);
		verifyManagerAuthority(project);

		Set<TimeUserModel> usersToAdd = userService.findAllByIds(userIds);

		project.setAssignedUsers(usersToAdd);

		return mapToResponse(projectRepository.save(project));
	}

	@Transactional
	public void archive(Long id) {
		ProjectModel project = getProject(id);
		verifyManagerAuthority(project);

		project.setActive(false);
		projectRepository.save(project);
	}

	private void verifyManagerAuthority(ProjectModel project) {
		TimeUserModel currentUser = userService.getCurrentUser();
		if (!project.getManager().getId().equals(currentUser.getId())
				&& currentUser.getRole() != Role.ROLE_ADMIN) {
			throw new AccessDeniedException("Du bist nicht berechtigt, dieses Projekt zu bearbeiten.");
		}
	}

	private ProjectResponse mapToResponse(ProjectModel project) {
		return new ProjectResponse(
				project.getId(),
				project.getName(),
				project.getDescription(),
				project.isActive(),
				project.getHourlyRate(),
				project.getCurrency()
		);
	}
}