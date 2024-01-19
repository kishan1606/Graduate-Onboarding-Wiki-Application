package com.cooksys.groupfinal.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_table")
@NoArgsConstructor
@Data
public class User {
	
	@Id
	@GeneratedValue
	private Long id;

	@Embedded
	private Credentials credentials;
	
	@Embedded
	private Profile profile;
	
	private boolean active;
	
	private boolean admin;
	
	private String status = "PENDING";
	
	@OneToMany(mappedBy = "author")
	@EqualsAndHashCode.Exclude
	private List<Announcement> announcements = new ArrayList<>();
	
	@ManyToMany(mappedBy = "employees")
	@EqualsAndHashCode.Exclude
	private List<Company> companies = new ArrayList<>();
	
	@ManyToMany(mappedBy = "teammates")
	@EqualsAndHashCode.Exclude
	private List<Team> teams = new ArrayList<>();

}
