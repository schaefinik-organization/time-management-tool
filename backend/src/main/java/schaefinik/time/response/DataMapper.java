package schaefinik.time.response;

import org.mapstruct.Mapper;
import schaefinik.time.model.ProjectModel;
import schaefinik.time.model.TimeEntryModel;
import schaefinik.time.model.TimeUserModel;
import schaefinik.time.response.entry.TimeEntryDTO;
import schaefinik.time.response.project.ProjectDTO;
import schaefinik.time.response.project.ProjectSummaryDTO;
import schaefinik.time.response.user.TimeUserDTO;
import schaefinik.time.response.user.UserSummaryDTO;

@Mapper(componentModel = "spring")
public interface DataMapper {

	UserSummaryDTO toUserSummaryDto(TimeUserModel model);

	ProjectSummaryDTO toProjectSummaryDto(ProjectModel model);

	TimeUserDTO toUserDto(TimeUserModel model);

	ProjectDTO toProjectDto(ProjectModel model);

	TimeEntryDTO toTimeEntryDto(TimeEntryModel model);
}