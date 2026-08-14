package schaefinik.time.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import schaefinik.time.enums.CurrencyCode;
import schaefinik.time.enums.Role;
import schaefinik.time.exception.ResourceNotFoundException;
import schaefinik.time.mapper.DataMapper;
import schaefinik.time.model.ProjectModel;
import schaefinik.time.model.TimeUserModel;
import schaefinik.time.repository.ProjectRepository;
import schaefinik.time.request.project.ProjectChangeRequest;
import schaefinik.time.request.project.ProjectCreateRequest;
import schaefinik.time.response.project.ProjectDTO;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

	private final ProjectRepository projectRepository;
	private final UserService userService;
	private final DataMapper dataMapper;

	@Transactional(readOnly = true)
	public List<ProjectDTO> findAllAssignedToCurrentUser() {
		TimeUserModel currentUser = userService.getCurrentUser();
		List<ProjectModel> assignedProjects = projectRepository.findByAssignedUsersContainingAndActiveIsTrue(currentUser);
		List<ProjectDTO> managedProjectsDTO = this.findAllManagedByCurrentUser();
		List<ProjectDTO> assignedProjectsDTO =
				Stream.of(assignedProjects)
						.flatMap(Collection::stream)
						.map(this::mapToDTO)
						.toList();
		return Stream.of(
						assignedProjectsDTO,
						managedProjectsDTO)
				.flatMap(Collection::stream)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<ProjectDTO> findAllManagedByCurrentUser() {
		TimeUserModel currentUser = userService.getCurrentUser();
		return projectRepository.findByManager(currentUser)
				.stream()
				.sorted((project1, project2) -> Boolean.compare(project2.isActive(), project1.isActive()))
				.map(this::mapToDTO)
				.toList();
	}

	@Transactional(readOnly = true)
	public ProjectDTO getProjectData(Long id) {
		return mapToDTO(getProject(id));
	}

	protected ProjectModel getProject(Long id) {
		return projectRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Projekt mit ID " + id + " nicht gefunden"));
	}

	@Transactional
	public ProjectDTO create(ProjectCreateRequest request) {
		TimeUserModel currentUser = userService.getCurrentUser();

		ProjectModel project = ProjectModel.builder()
				.name(request.getName())
				.description(request.getDescription())
				.manager(currentUser)
				.active(true)
				.hourlyRate(request.getHourlyRate() != null ? request.getHourlyRate() : BigDecimal.ZERO)
				.internalHourlyRate(request.getInternalHourlyRate() != null ? request.getInternalHourlyRate() : BigDecimal.ZERO)
				.currency(request.getCurrency() != null ? request.getCurrency() : CurrencyCode.EUR)
				.build();

		if (request.getUserIds() != null) {
			project.setAssignedUsers(userService.findAllByIds(request.getUserIds()));
		}

		return mapToDTO(projectRepository.save(project));
	}

	@Transactional
	public ProjectDTO update(Long id, ProjectChangeRequest request) {
		ProjectModel project = getProject(id);
		verifyManagerAuthority(project);

		project.setName(request.getName());
		project.setDescription(request.getDescription());

		if (request.getHourlyRate() != null) {
			project.setHourlyRate(request.getHourlyRate());
		}
		if (request.getInternalHourlyRate() != null) {
			project.setInternalHourlyRate(request.getInternalHourlyRate());
		}
		if (request.getCurrency() != null) {
			project.setCurrency(request.getCurrency());
		}
		if (request.getAssignedUserIds() != null) {
			project.setAssignedUsers(userService.findAllByIds(request.getAssignedUserIds()));
		}
		return mapToDTO(projectRepository.save(project));
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

	private ProjectDTO mapToDTO(ProjectModel projectModel) {
		return dataMapper.toProjectDto(projectModel);
	}
}