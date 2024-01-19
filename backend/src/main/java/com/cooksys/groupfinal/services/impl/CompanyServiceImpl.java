package com.cooksys.groupfinal.services.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.cooksys.groupfinal.dtos.AnnouncementDto;
import com.cooksys.groupfinal.dtos.BasicUserDto;
import com.cooksys.groupfinal.dtos.CompanyDto;
import com.cooksys.groupfinal.dtos.CredentialsDto;
import com.cooksys.groupfinal.dtos.FullUserDto;
import com.cooksys.groupfinal.dtos.ProfileDto;
import com.cooksys.groupfinal.dtos.ProjectDto;
import com.cooksys.groupfinal.dtos.TeamDto;
import com.cooksys.groupfinal.entities.Announcement;
import com.cooksys.groupfinal.entities.Company;
import com.cooksys.groupfinal.entities.Profile;
import com.cooksys.groupfinal.entities.Project;
import com.cooksys.groupfinal.entities.Team;
import com.cooksys.groupfinal.entities.User;
import com.cooksys.groupfinal.exceptions.BadRequestException;
import com.cooksys.groupfinal.exceptions.NotAuthorizedException;
import com.cooksys.groupfinal.exceptions.NotFoundException;
import com.cooksys.groupfinal.mappers.AnnouncementMapper;
import com.cooksys.groupfinal.mappers.BasicUserMapper;
import com.cooksys.groupfinal.mappers.CompanyMapper;
import com.cooksys.groupfinal.mappers.FullUserMapper;
import com.cooksys.groupfinal.mappers.ProfileMapper;
import com.cooksys.groupfinal.mappers.ProjectMapper;
import com.cooksys.groupfinal.mappers.TeamMapper;
import com.cooksys.groupfinal.repositories.CompanyRepository;
import com.cooksys.groupfinal.repositories.TeamRepository;
import com.cooksys.groupfinal.repositories.UserRepository;
import com.cooksys.groupfinal.services.CompanyService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
	
	private final CompanyMapper companyMapper;
	private final CompanyRepository companyRepository;
	private final TeamMapper teamMapper;
	private final TeamRepository teamRepository;
	private final BasicUserMapper basicUserMapper;
	private final FullUserMapper fullUserMapper;
	private final UserRepository userRepository;
	private final AnnouncementMapper announcementMapper;
	private final ProjectMapper projectMapper;
	private final ProfileMapper profileMapper;

	private Company findCompany(Long id) {
        Optional<Company> company = companyRepository.findById(id);
        if (company.isEmpty()) {
            throw new NotFoundException("A company with the provided id does not exist.");
        }
        return company.get();
    }
	
	private Team findTeam(Long id) {
        Optional<Team> team = teamRepository.findById(id);
        if (team.isEmpty()) {
            throw new NotFoundException("A team with the provided id does not exist.");
        }
        return team.get();
    }

	private ProfileDto getProfileDtoForUser(Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));

		return profileMapper.entityToDto(user.getProfile());
	}

	@Override
	public List<CompanyDto> getAllCompanies() {
		return companyMapper.entitiesToDtos(companyRepository.findAll());
	}

	@Override
	public List<BasicUserDto> getAllUsers(Long id) {
		Company company = findCompany(id);
		List<User> filteredUsers = new ArrayList<>();
		company.getEmployees().forEach(filteredUsers::add);
		filteredUsers.removeIf(user -> !user.isActive());
		return basicUserMapper.entitiesToBasicUserDtos(filteredUsers);
	}

	@Override
	public List<AnnouncementDto> getAllAnnouncements(Long id) {
		Company company = findCompany(id);
		List<Announcement> sortedList = new ArrayList<Announcement>(company.getAnnouncements());
		sortedList.sort(Comparator.comparing(Announcement::getDate).reversed());
		List<Announcement> sortedList2 = new ArrayList<Announcement>(sortedList);
		return announcementMapper.entitiesToDtos(sortedList2);
	}

	@Override
	public List<TeamDto> getAllTeams(Long id) {
		Company company = findCompany(id);
		return teamMapper.entitiesToDtos(company.getTeams());
	}

	@Override
	public List<ProjectDto> getAllProjects(Long companyId, Long teamId) {
		Company company = findCompany(companyId);
		Team team = findTeam(teamId);
		if (!company.getTeams().contains(team)) {
			throw new NotFoundException("A team with id " + teamId + " does not exist at company with id " + companyId + ".");
		}
		List<Project> filteredProjects = new ArrayList<>();
		team.getProjects().forEach(filteredProjects::add);
		filteredProjects.removeIf(project -> !project.isActive());
		return projectMapper.entitiesToDtos(filteredProjects);
	}

	@Override
	public List<FullUserDto> getAllTeamUsers(Long companyId, Long teamId) {
		Company company = findCompany(companyId);
		Team team = findTeam(teamId);
		if (!company.getTeams().contains(team)) {
			throw new NotFoundException("A team with id " + teamId + " does not exist at company with id " + companyId +".");
		}
		List<User> filteredUsers = new ArrayList<>();
		team.getTeammates().forEach(filteredUsers::add);
		filteredUsers.removeIf(user -> !user.isActive());
		return fullUserMapper.entitiesToFullUserDtos(filteredUsers);
	}

	@Override
	public TeamDto addTeamToCompany(Long companyId, TeamDto teamDto) {
		CredentialsDto credentials = teamDto.getCredentialsDto();
		if (credentials.getUsername() == null || credentials.getPassword() == null) {
			throw new BadRequestException("Valid credentials are required");
		}
		if (teamDto.getName() == null || teamDto.getDescription() == null) {
			throw new BadRequestException("Invalid information given.");
		}
		Optional<User> optionalUser = userRepository.findByCredentialsUsernameAndActiveTrue(credentials.getUsername());
		if (optionalUser.isEmpty()) {
			throw new NotFoundException("No user found with username: " + credentials.getUsername());
		}
		User user = optionalUser.get();
		if (!credentials.getPassword().equals(user.getCredentials().getPassword())) {
			throw new NotAuthorizedException("Not authorized");
		}

		Company company = companyRepository.findById(companyId)
				.orElseThrow(() -> new NotFoundException("Company with id " + companyId + " was not found."));
		Team newTeam = new Team();
		newTeam.setName(teamDto.getName());
		newTeam.setCompany(company);
		newTeam.setDescription(teamDto.getDescription());
		newTeam.setActive(true); // added for the changes to TeamDto for boolean - John
		if (teamDto.getTeammates() != null && !teamDto.getTeammates().isEmpty()) {
			List<User> teammates = basicUserMapper.basicDtosToEntities(teamDto.getTeammates());
			Set<User> uniqueTeammates = new HashSet<>();
			List<User> nonDuplicateTeammates = new ArrayList<>();
			for (User teammate : teammates) {
				if (uniqueTeammates.add(teammate)) {
					nonDuplicateTeammates.add(teammate);
				}
			}
			for (User teammate : nonDuplicateTeammates) {
				ProfileDto profileDto = getProfileDtoForUser(teammate.getId());
				if (profileDto != null) {
					Profile profile = profileMapper.dtoToEntity(profileDto);
					teammate.setProfile(profile);
				}
			}
			newTeam.setTeammates(teammates);
		} else {
			newTeam.setTeammates(new ArrayList<>());
		}
		Team savedTeam = teamRepository.saveAndFlush(newTeam);
		company.getTeams().add(savedTeam);
		companyRepository.saveAndFlush(company);
		return teamMapper.entityToDto(savedTeam);
	}

	@Override
	public CompanyDto getCompanyById(Long companyId) {
		Optional<Company> optionalCompany = companyRepository.findById(companyId);
		if (optionalCompany.isEmpty()) {
			throw new NotFoundException("No company found with id: " + companyId);
		}
		Company company = optionalCompany.get();
		return companyMapper.entityToDto(company);
	}

	@Override
	public CompanyDto deleteCompany(Long companyId, CredentialsDto credentialsDto) {
		Optional<User> admin = userRepository.findByCredentialsUsernameAndActiveTrue(credentialsDto.getUsername());
		if (admin == null || credentialsDto.getPassword() == null) {
			throw new BadRequestException("Valid credentials are required");
		}

		User adminCo = admin.get();
		if(!credentialsDto.getPassword().equals(adminCo.getCredentials().getPassword())) {
			throw new NotAuthorizedException("Not Authorized to perform the action.");
		}
		Optional<Company> companyToDelete = companyRepository.findById(companyId);
		if (companyToDelete.isEmpty()) {
			throw new NotFoundException("The companyId you would like to delete does noy exist in DB.");
		}

		Company company = companyToDelete.get();

		List<User> employeesToDelete = company.getEmployees();

		for (User employee : employeesToDelete) {
			List<Team> teams = employee.getTeams();
			for (Team team : teams) {
				List<User> teammates = new ArrayList<>(team.getTeammates());
				teammates.remove(employee);
				team.setTeammates(teammates);
			}
			teamRepository.saveAllAndFlush(teams);
		}

		for (User employee : employeesToDelete) {
			List<Company> companies = new ArrayList<>(employee.getCompanies());
			companies.remove(company);
			employee.setCompanies(companies);
		}
		userRepository.saveAllAndFlush(employeesToDelete);
		company.setActive(false);
		company.setEmployees(null);
		companyRepository.saveAndFlush(company);

		return companyMapper.entityToDto(company);
	}
}
