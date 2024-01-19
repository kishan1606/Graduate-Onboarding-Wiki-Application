package com.cooksys.groupfinal.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@NoArgsConstructor
@Data
public class ProjectDto {
	
	private Long id;
    
    private String name;
    
    private String description;
    
    private boolean active;

    private Timestamp created;
    private Timestamp lastUpdated;
    
    private TeamDto team;

}