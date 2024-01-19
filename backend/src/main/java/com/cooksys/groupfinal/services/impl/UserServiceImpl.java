package com.cooksys.groupfinal.services.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.cooksys.groupfinal.dtos.TeamDto;
import com.cooksys.groupfinal.dtos.UserRequestDto;
import com.cooksys.groupfinal.dtos.UserResponseDto;
import com.cooksys.groupfinal.mappers.TeamMapper;
import org.springframework.stereotype.Service;

import com.cooksys.groupfinal.dtos.CredentialsDto;
import com.cooksys.groupfinal.dtos.FullUserDto;
import com.cooksys.groupfinal.dtos.UserRequestDto;
import com.cooksys.groupfinal.entities.Company;
import com.cooksys.groupfinal.entities.Credentials;
import com.cooksys.groupfinal.entities.Team;
import com.cooksys.groupfinal.entities.User;
import com.cooksys.groupfinal.exceptions.BadRequestException;
import com.cooksys.groupfinal.exceptions.NotAuthorizedException;
import com.cooksys.groupfinal.exceptions.NotFoundException;
import com.cooksys.groupfinal.mappers.CredentialsMapper;
import com.cooksys.groupfinal.mappers.FullUserMapper;
import com.cooksys.groupfinal.mappers.UserMapper;
import com.cooksys.groupfinal.repositories.CompanyRepository;
import com.cooksys.groupfinal.repositories.TeamRepository;
import com.cooksys.groupfinal.repositories.UserRepository;
import com.cooksys.groupfinal.services.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final FullUserMapper fullUserMapper;
	private final CredentialsMapper credentialsMapper;
	private final UserMapper userMapper;
	private final CompanyRepository companyRepository;
	private final TeamRepository teamRepository;
  private final TeamMapper teamMapper;


	private User findUser(String username) {
		Optional<User> user = userRepository.findByCredentialsUsernameAndActiveTrue(username);
		if (user.isEmpty()) {
			throw new NotFoundException("The username provided does not belong to an active user.");
		}
		return user.get();
	}

	@Override
	public FullUserDto login(CredentialsDto credentialsDto) {
		if (credentialsDto == null || credentialsDto.getUsername() == null || credentialsDto.getPassword() == null) {
			throw new BadRequestException("A username and password are required.");
		}
		Credentials credentialsToValidate = credentialsMapper.dtoToEntity(credentialsDto);
		User userToValidate = findUser(credentialsDto.getUsername());
		if (!userToValidate.getCredentials().equals(credentialsToValidate)) {
			throw new NotAuthorizedException("The provided credentials are invalid.");
		}
		if (userToValidate.getStatus().equals("PENDING")) {
			userToValidate.setStatus("JOINED");
			userRepository.saveAndFlush(userToValidate);
		}
		return fullUserMapper.entityToFullUserDto(userToValidate);
	}

//    @Override
//    public List<UserResponseDto> getAllUsers() {
//        List<User> users = userRepository.findAll();
//        Set<User> uniqueUsers = new HashSet<>(users);
//        List<User> noDuplicateUsers = new ArrayList<>(uniqueUsers);
//        return userMapper.entitiesToDtos(noDuplicateUsers);
//
//    }

	@Override
	public List<FullUserDto> getAllUsers() {
		List<User> users = userRepository.findAll();
		Set<User> uniqueUsers = new HashSet<>(users);
		List<User> noDuplicateUsers = new ArrayList<>(uniqueUsers);
		return userMapper.entitiesToDtos(noDuplicateUsers);
	}

	@Override
	public FullUserDto createUser(UserRequestDto userRequestDto) {
		// request validation
		// make sure first name, last name, and email are given
		if (userRequestDto.getProfile().getFirstName() == null || userRequestDto.getProfile().getLastName() == null
				|| userRequestDto.getProfile().getEmail() == null) {
			throw new BadRequestException("First name, last name, and email are required.");
		}
		// make sure a password is passed
		if (userRequestDto.getCredentials().getUsername() == null
				|| userRequestDto.getCredentials().getPassword() == null) {
			throw new BadRequestException("A username and password is required to create a new user.");
		}
		// make sure that a company is passed in
		if (userRequestDto.getCompanyId() == null) {
			throw new BadRequestException("A company must be associated with a new user.");
		}
		
		// checks if username is already taken among both active and inactive users
		// theoretically should be taken care of in the frontend, so may be redundant
		Optional<User> optionalUser = userRepository
				.findByCredentialsUsername(userRequestDto.getCredentials().getUsername());
		if (optionalUser.isPresent()) {
			throw new BadRequestException("Username is already taken.");
		}

		// convert dto to entity
		User userToSave = fullUserMapper.requestDtoToEntity(userRequestDto);
		userToSave.setActive(true);
		
		// set company
		Optional<Company> optionalCompany = companyRepository.findById(userRequestDto.getCompanyId());
		if (optionalCompany.get() == null) {
			throw new BadRequestException("Company of ID " + userRequestDto.getCompanyId() + " does not exist.");
		}
		Company companyToAddToUser = optionalCompany.get();
		
		List<Company> companies = userToSave.getCompanies();
		companies.add(companyToAddToUser);
		userToSave.setCompanies(companies);
		User savedUser = userRepository.saveAndFlush(userToSave);

		// grab the company's employees
		List<User> currentEmployees = companyToAddToUser.getEmployees(); 
		// add this user to that list above and set it
		currentEmployees.add(savedUser);
		companyToAddToUser.setEmployees(currentEmployees);
		// save and flush
		companyRepository.saveAndFlush(companyToAddToUser);
		

		// convert it to a responseDto from entity and return that
		return fullUserMapper.entityToFullUserDto(savedUser);
	}

    @Override
    public List<TeamDto> findAllTeamsByUser(Long id) {
        Optional<User> userToFind = userRepository.findById(id);

        if (userToFind.isEmpty()) {
            throw new NotFoundException("This user Id is invalid");
        }

        //        if (!(userToFind.get().isActive())) {
//            throw new NotFoundException("User is inactive");
//        }
//        commented out for now pending discussions with frontend team

        List<TeamDto> listOfTeamsUserIsOn = teamMapper.entitiesToDtos(userToFind.get().getTeams());

        return listOfTeamsUserIsOn;

    }

    @Override
    public FullUserDto getUserById(Long id) {
        Optional<User> userToFind = userRepository.findById(id);

        if (userToFind.isEmpty()) {
            throw new NotFoundException("This user Id is invalid");
        }

//        if (!(userToFind.get().isActive())) {
//            throw new NotFoundException("User is inactive");
//        }
//        commented out for now pending discussions with frontend team

        FullUserDto fullUserDto = fullUserMapper.entityToFullUserDto(userToFind.get());

        if (fullUserDto.isAdmin()) {
            return fullUserDto;
        }
        // so if the user is not an admin, the idea here is to remove any data that they shouldn't be privy to
        // could modify it so it just returns an error, but this would give flexiblity for making the call
        fullUserDto.setTeams(new ArrayList<>());
        fullUserDto.setCompanies(new ArrayList<>());

        return fullUserDto;
    }

	@Override
	public UserResponseDto editUser(Long userId, UserRequestDto userRequestDto) {
		/* Currently finds only active users. Can be edited to find all users for use case*/
		Optional<User> userToEdit = userRepository.findByIdAndActiveTrue(userId);
        if (userToEdit.isEmpty()) {
            throw new NotFoundException("The id provided does not belong to an active user.");
        }
        
        /*Credentials must be passed in and cannot be left null. Can be changed to only update if edited*/
        if (userRequestDto.getCredentials() == null || userRequestDto.getProfile() == null) {
        	throw new BadRequestException("Missing credentials or profile to update");
        }        
        if (userRequestDto.getCredentials().getUsername() == null || userRequestDto.getCredentials().getPassword() == null) {
        	throw new BadRequestException("Missing Username or Password in the credentials");
        }
        
        User updateHolder = userMapper.dtoToEntity(userRequestDto);
        User userToSave = userToEdit.get();
        
        /*Check if username is available. Can be refactored once username validate endpoint is implemented*/
        if(updateHolder.getCredentials().getUsername() != userToSave.getCredentials().getUsername()) {
        	Optional<User> UsernameCheck = userRepository.findByCredentialsUsernameAndActiveTrue(updateHolder.getCredentials().getUsername());
        	if(UsernameCheck.isPresent() && (userToSave != UsernameCheck.get())) {
        		throw new BadRequestException("Username already exists!");
        	}
        }
        
        /*Update credentials. Check code is present in the case that use case changes to -> User can be updated without needed changes to Username and Password*/
        if(updateHolder.getCredentials().getUsername() != null) {
        	userToSave.getCredentials().setUsername(updateHolder.getCredentials().getUsername());
        }
        if(updateHolder.getCredentials().getPassword() != null) {
        	userToSave.getCredentials().setPassword(updateHolder.getCredentials().getPassword());
        }
        
        /*Update Profile*/
        if(updateHolder.getProfile().getEmail() != null) {
        	userToSave.getProfile().setEmail(updateHolder.getProfile().getEmail());
        }
        if(updateHolder.getProfile().getFirstName() != null) {
        	userToSave.getProfile().setFirstName(updateHolder.getProfile().getFirstName());
        }
        if(updateHolder.getProfile().getLastName() != null) {
        	userToSave.getProfile().setLastName(updateHolder.getProfile().getLastName());
        }
        if(updateHolder.getProfile().getPhone() != null) {
        	userToSave.getProfile().setPhone(updateHolder.getProfile().getPhone());
        }
        
        /*Update Admin Status if its different*/
        if(updateHolder.isAdmin() != userToSave.isAdmin()) {
        	userToSave.setAdmin(updateHolder.isAdmin());
        }
        
        return userMapper.entityToResponseDto(userRepository.saveAndFlush(userToSave));
	}

	@Override
	public FullUserDto deleteUser(Long userId, CredentialsDto credentialDto) {
		if(credentialDto.getUsername().isEmpty() || credentialDto.getPassword().isEmpty()) {
			throw new BadRequestException("Please enter some credentials");
		}
		
		Optional<User> adminUser = userRepository.findByCredentialsUsernameAndActiveTrue(credentialDto.getUsername());
		Optional<User> userToDelete = userRepository.findByIdAndActiveTrue(userId);
		
		if(adminUser.isEmpty()) {
			throw new NotFoundException("You must be an active admin to access this");
		}
		if(!adminUser.get().getCredentials().getPassword().equals(credentialDto.getPassword())) {
			System.out.println(adminUser.get().getCredentials().getPassword() + " Does not match: " + credentialDto.getPassword());
			throw new BadRequestException("Bad Credentials");
		}
		if(userToDelete.isEmpty()) {
			throw new NotFoundException("The user to delete does not exist or is already set to inactive.");
		}
		
		
		List<Company> checkCompanys = userToDelete.get().getCompanies();
		List<Team> checkTeams = userToDelete.get().getTeams();
		
		
		for(Team team: checkTeams) {
			List<User> newTeam = team.getTeammates();
			newTeam.remove(userToDelete.get());
			team.setTeammates(newTeam);
		}
		teamRepository.saveAllAndFlush(checkTeams);		
		for (Company company: checkCompanys) {
			List<User> newPayroll = company.getEmployees();
			newPayroll.remove(userToDelete.get());
			company.setEmployees(newPayroll);
		}
		companyRepository.saveAllAndFlush(checkCompanys);	
		
		userToDelete.get().setActive(false);
		userToDelete.get().setCompanies(null);
		userToDelete.get().setTeams(null);
		userRepository.saveAndFlush(userToDelete.get());
		
		return userMapper.entityToDto(userRepository.findById(userId).get());
	}


}
