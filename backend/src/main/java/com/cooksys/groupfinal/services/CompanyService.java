package com.cooksys.groupfinal.services;

import java.util.List;

import com.cooksys.groupfinal.dtos.*;

public interface CompanyService {

	List<BasicUserDto> getAllUsers(Long id);

	List<AnnouncementDto> getAllAnnouncements(Long id);

	List<TeamDto> getAllTeams(Long id);

	List<ProjectDto> getAllProjects(Long companyId, Long teamId);

	List<FullUserDto> getAllTeamUsers(Long companyId, Long teamId);

    List<CompanyDto> getAllCompanies();

	TeamDto addTeamToCompany(Long companyId, TeamDto teamDto);

	CompanyDto getCompanyById(Long companyId);

	CompanyDto deleteCompany(Long companyId, CredentialsDto credentialsDto);
}
