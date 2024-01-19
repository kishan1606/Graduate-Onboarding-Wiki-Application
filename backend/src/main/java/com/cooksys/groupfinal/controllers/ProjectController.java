package com.cooksys.groupfinal.controllers;


import com.cooksys.groupfinal.dtos.CredentialsDto;
import com.cooksys.groupfinal.dtos.ProjectDto;
import org.springframework.web.bind.annotation.*;

import com.cooksys.groupfinal.dtos.ProjectDto;
import com.cooksys.groupfinal.dtos.ProjectRequestDto;
import com.cooksys.groupfinal.services.ProjectService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@CrossOrigin(origins="*")
public class ProjectController {
	
	
	 private final ProjectService projectService;

	@GetMapping("/{id}")
	public ProjectDto getProjectById(@PathVariable Long id) {
		return projectService.findProjectById(id);
	}


	@PatchMapping("/{id}")
	public ProjectDto updateProject(@PathVariable Long id, @RequestBody ProjectRequestDto projectRequestDto) {
		return projectService.updateProject(id, projectRequestDto);
	}

	@DeleteMapping("/{id}")
	public boolean deleteProject(@PathVariable Long id) {
		// will add credentials as a request body if its deemed necessary, but for now will hold off
		// could change to patch mapping since it's not a pure delete, not sure what standard we want for that
		return projectService.deleteProject(id);
	}

}
