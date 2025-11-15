package com.studyhub.track.application.service;

import com.studyhub.track.domain.model.modul.Modul;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ModulUpdateService {

	private final Logger log = LoggerFactory.getLogger(ModulService.class);
	private final ModulRepository modulRepository;

	public ModulUpdateService(ModulRepository modulRepository) {
		this.modulRepository = modulRepository;
	}

	@Transactional
	public void updateSeconds(UUID fachId, int seconds) throws ModulSecondsUpdateException {
		Modul modul = modulRepository.findByUuid(fachId);
		modul.addSeconds(seconds);
		Modul saved = modulRepository.save(modul);

		if (saved == null) throw new ModulSecondsUpdateException("could not update seconds for modul with id: %s".formatted(fachId.toString()));

		log.info("updated modul with id:%s to seconds=%s".formatted(fachId.toString(), String.valueOf(seconds)));
	}

}
