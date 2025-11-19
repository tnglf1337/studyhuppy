package com.studyhub.track.adapter.kafka;

import com.studyhub.kafka.dto.UserDto;
import com.studyhub.track.application.service.IUserDeletionConsumer;
import com.studyhub.track.application.service.LernplanService;
import com.studyhub.track.application.service.ModulService;
import com.studyhub.track.application.service.SessionService;
import com.studyhub.track.domain.model.lernplan.Lernplan;
import com.studyhub.track.domain.model.modul.Modul;
import com.studyhub.track.domain.model.session.Session;
import com.studyhub.track.domain.service.SessionLoeschenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Consumer class for user deletion events produced by the authentication service.
 * All user data will be deleted.
 * topic: 'user-deletion'
 */
@Profile("dev-kafka")
@Component
public class UserDeletionConsumerImpl implements IUserDeletionConsumer {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserDeletionConsumerImpl.class);

	private final ModulService modulService;
	private final LernplanService lernplanService;
	private final SessionService sessionService;
	private final SessionLoeschenService sessionLoeschenService;

	public UserDeletionConsumerImpl(
			ModulService modulService,
			LernplanService lernplanService,
			SessionService sessionService, SessionLoeschenService sessionLoeschenService) {
		this.modulService = modulService;
		this.lernplanService = lernplanService;
		this.sessionService = sessionService;
		this.sessionLoeschenService = sessionLoeschenService;
	}

	@Override
	@KafkaListener(
			topics="user-deletion",
			groupId ="studyhuppy-modul"
	)
	public void consumeUserDeletion(UserDto userDto) {
		deleteAllUserData(userDto);
	}

	/**
	 * Deletes all user data associated with the given userDto.
	 * @param userDto The <code>UserDto</code>> containing the username
	 */
	@Override
	public void deleteAllUserData(UserDto userDto) {
		String username = userDto.username();
		List<Modul> allModules = modulService.findAllByUsername(username);
		List<Lernplan> allLernplaene = lernplanService.findAllLernplaeneByUsername(username);
		List<Session> allSession = sessionService.getSessionsByUsername(username);

		for (Modul modul : allModules) modulService.deleteModul(modul.getFachId(), userDto.username());
		for(Lernplan lernplan : allLernplaene) lernplanService.deleteLernplanByFachId(lernplan.getFachId());
		for (Session session : allSession) sessionLoeschenService.sessionLoeschen(session.getFachId(), username);

		LOGGER.info("Deleted all associated user data from service 'modul'");
	}
}