package com.cooksys.groupfinal.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
public class Team {
	
	@Id
	@GeneratedValue
	private Long id;
	
	private String name;
	
	private String description;

	private boolean isActive;
	
	@ManyToOne
	private Company company;
	
	@ManyToMany
	@EqualsAndHashCode.Exclude
	private List<User> teammates = new ArrayList<>();
	
	@OneToMany(mappedBy = "team")
	@EqualsAndHashCode.Exclude
	private List<Project> projects = new ArrayList<>();

}
