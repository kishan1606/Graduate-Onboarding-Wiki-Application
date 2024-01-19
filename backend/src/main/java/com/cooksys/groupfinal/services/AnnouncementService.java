package com.cooksys.groupfinal.services;

import com.cooksys.groupfinal.dtos.AnnouncementDto;
import com.cooksys.groupfinal.dtos.AnnouncementRequestDto;
import com.cooksys.groupfinal.dtos.CredentialsDto;

public interface AnnouncementService {

    AnnouncementDto addAnnouncement(Long companyId, AnnouncementRequestDto announcementRequest);

    AnnouncementDto updateAnnouncement(Long announcementId, AnnouncementRequestDto announcementRequest);

    AnnouncementDto deleteAnnouncementById(Long announcementId, CredentialsDto credentialsRequest);
}
