package com.cooksys.groupfinal.dtos;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CompanyDto {
	
	private Long id;
    
    private String name;
    
    private String description;
    
    private List<TeamDto> teams;
    
    private List<BasicUserDto> employees;

}