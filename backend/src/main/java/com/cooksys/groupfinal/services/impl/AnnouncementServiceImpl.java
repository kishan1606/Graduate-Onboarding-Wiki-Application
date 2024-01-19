package com.cooksys.groupfinal.services.impl;

import com.cooksys.groupfinal.dtos.AnnouncementDto;
import com.cooksys.groupfinal.dtos.AnnouncementRequestDto;
import com.cooksys.groupfinal.dtos.CredentialsDto;
import com.cooksys.groupfinal.dtos.UserRequestDto;
import com.cooksys.groupfinal.entities.Announcement;
import com.cooksys.groupfinal.entities.Company;
import com.cooksys.groupfinal.entities.Credentials;
import com.cooksys.groupfinal.entities.User;
import com.cooksys.groupfinal.exceptions.BadRequestException;
import com.cooksys.groupfinal.exceptions.NotAuthorizedException;
import com.cooksys.groupfinal.exceptions.NotFoundException;
import com.cooksys.groupfinal.mappers.AnnouncementMapper;
import com.cooksys.groupfinal.mappers.CredentialsMapper;
import com.cooksys.groupfinal.repositories.AnnouncementRepository;
import com.cooksys.groupfinal.repositories.CompanyRepository;
import com.cooksys.groupfinal.repositories.UserRepository;
import com.cooksys.groupfinal.services.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {
    private final AnnouncementMapper announcementMapper;
    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final CredentialsMapper credentialsMapper;

    @Override
    public AnnouncementDto addAnnouncement(Long companyId, AnnouncementRequestDto announcementRequest) {
        Optional<Company> optionalCompany = companyRepository.findById(companyId);
        if (optionalCompany.isEmpty()) {
            throw new NotFoundException("No company found with id: " + companyId);
        }
        Company company = optionalCompany.get();
        CredentialsDto credentials = announcementRequest.getCredentials();
        if (credentials.getUsername() == null || credentials.getPassword() == null) {
            throw new BadRequestException("Valid credentials are required");
        }
        if (announcementRequest.getTitle() == null || announcementRequest.getMessage() == null) {
            throw new BadRequestException("A title and message are required");
        }
        Optional<User> optionalUser = userRepository.findByCredentialsUsernameAndActiveTrue(credentials.getUsername());
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("No user found with username: " + credentials.getUsername());
        }
        User user = optionalUser.get();
        if (!credentials.getPassword().equals(user.getCredentials().getPassword())) {
            throw new NotAuthorizedException("You are not authorized to perform this action");
        }
        Announcement announcementToAdd = announcementMapper.requestToEntity(announcementRequest);
        announcementToAdd.setAuthor(user);
        announcementToAdd.setCompany(company);
        return announcementMapper.entityToDto(announcementRepository.saveAndFlush(announcementToAdd));
    }

    @Override
    public AnnouncementDto updateAnnouncement(Long announcementId, AnnouncementRequestDto announcementRequest) {
        Optional<Announcement> optionalAnnouncement = announcementRepository.findById(announcementId);
        if (optionalAnnouncement.isEmpty()) {
            throw new NotFoundException("No announcement found with id: " + announcementId);
        }
        Credentials credentials = credentialsMapper.dtoToEntity(announcementRequest.getCredentials());
        if (announcementRequest.getCredentials() == null || credentials.getUsername() == null || credentials.getPassword() == null) {
            throw new BadRequestException("Valid credentials are required");
        }
        if (announcementRequest.getTitle() == null || announcementRequest.getMessage() == null) {
            throw new BadRequestException("A title and message are required");
        }
        Optional<User> optionalUser = userRepository.findByCredentialsUsernameAndActiveTrue(credentials.getUsername());
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("No user found with username: " + credentials.getUsername());
        }
        User user = optionalUser.get();
        if (!credentials.getPassword().equals(user.getCredentials().getPassword())) {
            throw new NotAuthorizedException("You are not authorized to perform this action");
        }
        Announcement announcementToUpdate = optionalAnnouncement.get();
        announcementToUpdate.setTitle(announcementRequest.getTitle());
        announcementToUpdate.setMessage(announcementRequest.getMessage());
        return announcementMapper.entityToDto(announcementRepository.saveAndFlush(announcementToUpdate));
    }

    @Override
    public AnnouncementDto deleteAnnouncementById(Long announcementId, CredentialsDto credentialsRequest) {
        Optional<Announcement> optionalAnnouncement = announcementRepository.findById(announcementId);
        if (optionalAnnouncement.isEmpty() || optionalAnnouncement.get().getDeleted()) {
            throw new NotFoundException("No announcement found with id: " + announcementId);
        }
        if (credentialsRequest == null || credentialsRequest.getUsername() == null || credentialsRequest.getPassword() == null) {
            throw new BadRequestException("Valid credentials are required to perform this action");
        }
        Optional<User> optionalUser = userRepository.findByCredentialsUsernameAndActiveTrue(credentialsRequest.getUsername());
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("No user found with username: " + credentialsRequest.getUsername());
        }
        User user = optionalUser.get();
        if (!user.getCredentials().getPassword().equals(credentialsRequest.getPassword())) {
            throw new NotAuthorizedException("You are not authorized to perform this action");
        }
        Announcement announcementToDelete = optionalAnnouncement.get();
        announcementToDelete.setDeleted(true);
        return announcementMapper.entityToDto(announcementRepository.saveAndFlush(announcementToDelete));
    }
}