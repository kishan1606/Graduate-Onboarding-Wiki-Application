package com.cooksys.groupfinal.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.cooksys.groupfinal.dtos.BasicUserDto;
import com.cooksys.groupfinal.dtos.UserRequestDto;
import com.cooksys.groupfinal.entities.User;

@Mapper(componentModel = "spring", uses = { ProfileMapper.class, CredentialsMapper.class })
public interface BasicUserMapper {

    BasicUserDto entityToBasicUserDto(User user);
    
    List<BasicUserDto> entitiesToBasicUserDtos(List<User> filteredUsers);
    
    User requestDtoToEntity(UserRequestDto userRequestDto);

    List<User> basicDtosToEntities(List<BasicUserDto> teammates);
}
