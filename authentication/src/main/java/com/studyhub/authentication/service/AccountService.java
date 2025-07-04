package com.studyhub.authentication.service;

import com.studyhub.authentication.adapter.kartei.KarteiRequestService;
import com.studyhub.authentication.adapter.mail.MailRequestService;
import com.studyhub.authentication.adapter.track.TrackRequestService;
import com.studyhub.authentication.db.AppUserRepository;
import com.studyhub.authentication.model.AppUser;
import com.studyhub.authentication.web.EmailChangeRequest;
import com.studyhub.authentication.config.JWTService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AccountService {

	private final AppUserRepository appUserRepository;
	private final TrackRequestService trackRequestService;
	private final KarteiRequestService karteiRequestService;
	private final MailRequestService mailRequestService;
	private final JWTService jwtService;
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public AccountService(AppUserRepository appUserRepository, TrackRequestService trackRequestService, KarteiRequestService karteiRequestService, MailRequestService mailRequestService, JWTService jwtService) {
		this.appUserRepository = appUserRepository;
		this.trackRequestService = trackRequestService;
		this.karteiRequestService = karteiRequestService;
		this.mailRequestService = mailRequestService;
		this.jwtService = jwtService;
	}

	//NOTE: was passiert, wenn in nur einem system ein erfolg ist und wieder getried wird?
	//TODO: die Requests an die Systeme implementieren
	public boolean deleteAccount(UUID userId, int tries) {
		boolean trackRequestSuccess = true; //trackRequestService.sendDeleteAllRequest(username);
		boolean karteiRequestSuccess = true; //karteiRequestService.sendDeleteAllRequest(username);
		appUserRepository.deleteByUserId(userId);

		boolean success = trackRequestSuccess && karteiRequestSuccess;

        log.info("User '%s' deleted from system (tries: %d)".formatted(userId, tries));
        return success;
	}

	public void editNotificationSubscription(Boolean activate, String username) {
		appUserRepository.updateNotificationSubscription(activate, username);
		log.info("updated notification subscription for user '%s' to '%s'".formatted(username, activate));
	}

	public Boolean getNotificationSubscription(String username) {
		return appUserRepository.getNotificationSubscription(username);
	}

	public AppUser findByUsername(String username) {
		return appUserRepository.findByUsername(username);
	}

	public void changePassword(String newPassword, String userId) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String encodedPassword = encoder.encode(newPassword);

		appUserRepository.updatePassword(encodedPassword, UUID.fromString(userId));
		log.info("user '%s' updates his/her password".formatted(userId));
	}

	public Integer findSemesterByUsername(String username) {
		return appUserRepository.findSemesterByUsername(username);
	}

	public Map<String, String> getUsersWithNotificationOn() {
		List<AppUser> users = appUserRepository.getUsersWithNotificationOn();
		Map<String, String> userMap = new HashMap<>();

		for (AppUser currUser : users) userMap.put(currUser.getUsername(), currUser.getMail());

		return userMap;
	}

	public boolean emailExists(String email) {
		return appUserRepository.findByMail(email) != null;
	}

	public void changeMail(EmailChangeRequest req) {
		int res = appUserRepository.updateMailByUserId(req.getNewMail(), UUID.fromString(req.getUserId()));
		if(res == 1) {
			log.info("SUCCESS changed email for user '%s'".formatted(req.getUsername()));
		} else if (res == 0) {
			log.error("Could not change email for user '%s'".formatted(req.getUsername()));
			throw new EmailChangeException("could not change user email because likely an error while updating in database occurred.");
		}
	}

	public boolean validPassword(String sendPw, String userId) {
		AppUser appUser = appUserRepository.findByUserId(UUID.fromString(userId));
		String retrievedPw = appUser.getPassword();
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
        return encoder.matches(sendPw, retrievedPw);
    }
}
