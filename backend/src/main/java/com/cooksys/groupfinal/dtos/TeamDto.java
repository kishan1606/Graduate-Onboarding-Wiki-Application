package com.cooksys.groupfinal.dtos;

import java.util.List;
import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TeamDto {

    private Long id;

    private String name;

    private String description;

    private boolean isActive;

    private List<BasicUserDto> teammates;

    private CredentialsDto credentialsDto;

}
