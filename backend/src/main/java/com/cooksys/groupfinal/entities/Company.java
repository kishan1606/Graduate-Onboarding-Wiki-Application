package com.cooksys.groupfinal.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
public class Company {
	
	@Id
	@GeneratedValue
	private Long id;
	
	private String name;
	
	private String description;

	private boolean active = true;
	
	@OneToMany(mappedBy = "company")
	@EqualsAndHashCode.Exclude
	private List<Announcement> announcements = new ArrayList<>();
	
	@ManyToMany
	@EqualsAndHashCode.Exclude
	private List<User> employees = new ArrayList<>();
	
	@OneToMany(mappedBy = "company")
	@EqualsAndHashCode.Exclude
	private List<Team> teams = new ArrayList<>();

}
