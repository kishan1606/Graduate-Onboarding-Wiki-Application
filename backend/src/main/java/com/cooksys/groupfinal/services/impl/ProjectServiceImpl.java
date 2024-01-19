package com.cooksys.groupfinal.services.impl;
import com.cooksys.groupfinal.exceptions.NotFoundException;
import java.util.Optional;

import com.cooksys.groupfinal.mappers.TeamMapper;
import org.springframework.stereotype.Service;
import com.cooksys.groupfinal.dtos.ProjectDto;
import com.cooksys.groupfinal.dtos.ProjectRequestDto;
import com.cooksys.groupfinal.entities.Project;
import com.cooksys.groupfinal.exceptions.BadRequestException;
import com.cooksys.groupfinal.mappers.ProjectMapper;
import com.cooksys.groupfinal.repositories.ProjectRepository;
import com.cooksys.groupfinal.services.ProjectService;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
	
	

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    private final TeamMapper teamMapper;

    @Override
    public ProjectDto findProjectById(Long id) {
        Optional<Project> projectToFind = projectRepository.findById(id);
        if (projectToFind.isEmpty()) {
            throw new NotFoundException("This project doesn't exist");
            // I'm not sure if we will need to throw this error if this method is purely for rendering
            // so can remove it if need be, depending on how the front end team goes about their error handling
        }
        // might add an isActive error handling as well

        return projectMapper.entityToDto(projectToFind.get());
    }

    @Override
    public ProjectDto updateProject(Long id, ProjectRequestDto projectRequestDto) {
        // I could also get the id from the project Dto if we want to limit the variables passed in
        Optional<Project> projectToFind = projectRepository.findById(id);
        if (projectToFind.isEmpty()) {
            throw new NotFoundException("This project doesn't exist");
        }
        Project projectToEdit = projectToFind.get();
        projectToEdit.setName(projectRequestDto.getName());
        // description, active, lastUpdated, team
        projectToEdit.setDescription(projectRequestDto.getDescription());
        // can use the timestamps for determining whether a user is active with some app logic here as well
//        projectToEdit.setTeam(teamMapper.dtoToEntity(projectDto.getTeam()));
        // maybe I add a save team functionality as well, or do an edit team, don't think its necessary tho
        return projectMapper.entityToDto(projectRepository.saveAndFlush(projectToEdit));

    }

    @Override
    public boolean deleteProject(Long id) {
        Optional<Project> projectToFind = projectRepository.findById(id);
        if (projectToFind.isEmpty()) {
            throw new NotFoundException("This project doesn't exist");
            // this likely won't come up because the user will be clicking on the project from the front-end
        }
        Project projectToDelete = projectToFind.get();
        projectToDelete.setActive(false);
        Project deletedProject = projectRepository.saveAndFlush(projectToDelete);

        return (deletedProject.isActive() == false);
        // shows that the project was successfully deleted/inactivated
    }
}
