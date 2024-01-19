package com.cooksys.groupfinal.services;
import com.cooksys.groupfinal.dtos.ProjectRequestDto;
import com.cooksys.groupfinal.dtos.ProjectDto;
import com.cooksys.groupfinal.entities.Project;


public interface ProjectService {

    public ProjectDto findProjectById(Long id);

    public ProjectDto updateProject(Long id, ProjectRequestDto projectRequestDto);

    public boolean deleteProject(Long id);
}
