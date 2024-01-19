package com.cooksys.groupfinal.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;

import com.cooksys.groupfinal.dtos.TeamDto;
import com.cooksys.groupfinal.dtos.UserRequestDto;

import org.springframework.web.bind.annotation.*;
import com.cooksys.groupfinal.dtos.UserResponseDto;

import com.cooksys.groupfinal.dtos.CredentialsDto;
import com.cooksys.groupfinal.dtos.FullUserDto;
import com.cooksys.groupfinal.dtos.UserRequestDto;
import com.cooksys.groupfinal.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins="*")
public class UserController {
	
	private final UserService userService;
	
	@PostMapping("/login")
    public FullUserDto login(@RequestBody CredentialsDto credentialsDto) {
        return userService.login(credentialsDto);
    }
	
	@PatchMapping("/{userId}")
	public UserResponseDto editUser(@PathVariable Long userId, @RequestBody UserRequestDto userRequestDto) {
		return userService.editUser(userId,userRequestDto);
	}

//    @GetMapping
//    public List<UserResponseDto> getAllUsers() {
//        return userService.getAllUsers();
//    }

    @GetMapping
    public List<FullUserDto> getAllUsers() {
        return userService.getAllUsers();
    }
    
    @PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public FullUserDto createUser(@RequestBody UserRequestDto userRequestDto) {
		return userService.createUser(userRequestDto);
	}

    @GetMapping("/{id}/teams")
    public List<TeamDto> getAllTeamsUserIsOn(@PathVariable Long id) {

        return userService.findAllTeamsByUser(id);

    }

    @GetMapping("/{id}")
    public FullUserDto getUserById(@PathVariable Long id) {

        return userService.getUserById(id);
        // check if user is admin or not
        // returns 2 types of userDtos depending on it
        // because I can't return two different types, I opted to return a fullUserDto
        // and we can basically delete any info that the non admin shouldn't see before returning the DTO
    }
    
    @DeleteMapping("/{userId}")
    public FullUserDto deleteUser(@PathVariable Long userId, @RequestBody CredentialsDto credentialsDto) {
    	return userService.deleteUser(userId,credentialsDto);
    }

}





