package com.cooksys.groupfinal.mappers;

import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;

import com.cooksys.groupfinal.dtos.ProjectDto;
import com.cooksys.groupfinal.dtos.ProjectRequestDto;
import com.cooksys.groupfinal.entities.Project;

@Mapper(componentModel = "spring", uses = { TeamMapper.class })
public interface ProjectMapper {
	
	ProjectDto entityToDto(Project project);

    List<ProjectDto> entitiesToDtos(List<Project> filteredProjects);
    
    Project requestDtoToEntity(ProjectRequestDto projectRequestDto);

}
