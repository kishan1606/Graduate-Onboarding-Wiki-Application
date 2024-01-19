package com.cooksys.groupfinal.controllers;

import org.springframework.web.bind.annotation.*;

import com.cooksys.groupfinal.dtos.ProjectDto;
import com.cooksys.groupfinal.dtos.ProjectRequestDto;
import com.cooksys.groupfinal.services.TeamService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
@CrossOrigin(origins="*")
public class TeamController {
	
	private final TeamService teamService;
	
	@PostMapping("/{teamId}/projects") public ProjectDto createProject(@PathVariable Long teamId, @RequestBody ProjectRequestDto projectRequestDto) {
		return teamService.createProject(teamId, projectRequestDto);
	}

	@DeleteMapping("/{teamId}")
	public boolean deleteTeam(@PathVariable Long teamId) {
		return teamService.deleteTeam(teamId);
	}

}
