package com.cooksys.groupfinal.mappers;

import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;

import com.cooksys.groupfinal.dtos.CompanyDto;
import com.cooksys.groupfinal.entities.Company;

@Mapper(componentModel = "spring", uses = { TeamMapper.class, BasicUserMapper.class })
public interface CompanyMapper {
	
	CompanyDto entityToDto(Company company);

	List<CompanyDto> entitiesToDtos(List<Company> companies);

}
