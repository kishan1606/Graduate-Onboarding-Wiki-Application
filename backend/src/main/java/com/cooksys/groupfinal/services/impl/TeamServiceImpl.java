package com.cooksys.groupfinal.services.impl;

import java.util.List;
import java.util.Optional;

import com.cooksys.groupfinal.dtos.TeamDto;
import org.springframework.stereotype.Service;

import com.cooksys.groupfinal.dtos.CredentialsDto;
import com.cooksys.groupfinal.dtos.ProjectDto;
import com.cooksys.groupfinal.dtos.ProjectRequestDto;
import com.cooksys.groupfinal.entities.Project;
import com.cooksys.groupfinal.entities.Team;
import com.cooksys.groupfinal.exceptions.BadRequestException;
import com.cooksys.groupfinal.exceptions.NotFoundException;
import com.cooksys.groupfinal.mappers.ProjectMapper;
import com.cooksys.groupfinal.repositories.ProjectRepository;
import com.cooksys.groupfinal.repositories.TeamRepository;
import com.cooksys.groupfinal.services.TeamService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {
	
	private final TeamRepository teamRepository;
	private final ProjectRepository projectRepository;
	private final ProjectMapper projectMapper;

	@Override
	public ProjectDto createProject(Long teamId, ProjectRequestDto projectRequestDto) {
		if (projectRequestDto.getName() == null || projectRequestDto.getDescription() == null) {
			throw new BadRequestException("Missing project name or description"); 
		}
		if (teamRepository.findById(teamId) == null) {
			throw new NotFoundException("No team found with id " + teamId);
		}
		CredentialsDto credentials = projectRequestDto.getCredentials();
		if (credentials == null) {
			throw new BadRequestException("Valid credentials are required");
		} else if (credentials.getUsername() == null || credentials.getPassword() == null) {
			throw new BadRequestException("Valid credentials are required");
		}
		Optional<Project> optionalProject = projectRepository.findByName(projectRequestDto.getName()); 
		Team team = teamRepository.findById(teamId).get();
		if (optionalProject.isEmpty()) {
			Project project = projectMapper.requestDtoToEntity(projectRequestDto);
			project.setActive(true);
			project.setTeam(team);
			projectRepository.saveAndFlush(project);
			List<Project> teamProjects = team.getProjects();
			teamProjects.add(project);
			team.setProjects(teamProjects);
			teamRepository.saveAndFlush(team);
			return projectMapper.entityToDto(project); 
		}
		throw new BadRequestException("Project already exists");
	}

	@Override
	public boolean deleteTeam(Long teamId) {

		Optional<Team> teamToFind = teamRepository.findById(teamId);
		if (teamToFind.isEmpty()) {
			throw new NotFoundException("This team doesn't exist");
			// unlike to hit given user scenario
		}

		Team teamToDelete = teamToFind.get();

		if (!teamToDelete.isActive()) {
			throw new BadRequestException("This team is already deleted");
		}

		teamToDelete.setActive(false);

		Team deletedTeam = teamRepository.saveAndFlush(teamToDelete);

		return (deletedTeam.isActive() == false);
	}

}
