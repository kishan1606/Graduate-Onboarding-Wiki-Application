package com.cooksys.groupfinal.controllers;

import com.cooksys.groupfinal.dtos.AnnouncementDto;
import com.cooksys.groupfinal.dtos.AnnouncementRequestDto;
import com.cooksys.groupfinal.dtos.CredentialsDto;
import org.springframework.web.bind.annotation.*;

import com.cooksys.groupfinal.services.AnnouncementService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/announcements")
@RequiredArgsConstructor
@CrossOrigin(origins="*")
public class AnnouncementController {
	
	private final AnnouncementService announcementService;

	@PostMapping("/{companyId}")
	public AnnouncementDto addAnnouncement(@PathVariable Long companyId, @RequestBody AnnouncementRequestDto announcementRequest) {
		return announcementService.addAnnouncement(companyId, announcementRequest);
	}

	@PatchMapping("/{announcementId}")
	public AnnouncementDto updateAnnouncement(@PathVariable Long announcementId, @RequestBody AnnouncementRequestDto announcementRequest) {
		return announcementService.updateAnnouncement(announcementId, announcementRequest);
	}

	@DeleteMapping("/{announcementId}")
	public AnnouncementDto deleteAnnouncement(@PathVariable Long announcementId, @RequestBody CredentialsDto credentialsRequest) {
		return announcementService.deleteAnnouncementById(announcementId, credentialsRequest);
	}

}
