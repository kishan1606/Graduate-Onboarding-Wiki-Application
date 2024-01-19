package com.cooksys.groupfinal.services;

import com.cooksys.groupfinal.dtos.ProjectDto;
import com.cooksys.groupfinal.dtos.ProjectRequestDto;

public interface TeamService {

	ProjectDto createProject(Long teamId, ProjectRequestDto projectRequestDto);

	boolean deleteTeam(Long teamId);

}
