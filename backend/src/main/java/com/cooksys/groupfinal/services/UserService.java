package com.cooksys.groupfinal.services;

import java.util.List;

import com.cooksys.groupfinal.dtos.CredentialsDto;
import com.cooksys.groupfinal.dtos.FullUserDto;
import com.cooksys.groupfinal.dtos.UserRequestDto;
import com.cooksys.groupfinal.dtos.TeamDto;
import com.cooksys.groupfinal.dtos.UserResponseDto;



public interface UserService {

	FullUserDto login(CredentialsDto credentialsDto);

//	List<UserResponseDto> getAllUsers();

	List<FullUserDto> getAllUsers();

	FullUserDto createUser(UserRequestDto userRequestDto);

	List<TeamDto> findAllTeamsByUser(Long id);

	FullUserDto getUserById(Long id);

	UserResponseDto editUser(Long userId, UserRequestDto userRequestDto);

	FullUserDto deleteUser(Long userId, CredentialsDto credentialsDto);

}

