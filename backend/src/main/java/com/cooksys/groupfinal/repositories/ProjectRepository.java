package com.cooksys.groupfinal.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cooksys.groupfinal.entities.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
	
	Optional<Project> findByName(String name);

}