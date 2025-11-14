package com.studyhub.track.application.service;

import com.studyhub.track.domain.model.modul.Modul;
import com.studyhub.track.domain.model.modul.ModulGelerntEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;

@Service
public class ModulEventService {
	private final ModulGelerntEventRepository modulGelerntEvent;
	private final ModulRepository modulRepository;
	private final DateProvider dateProvider;
	private final Logger logger = LoggerFactory.getLogger(ModulEventService.class);

	public ModulEventService(ModulGelerntEventRepository modulGelerntEvent, ModulRepository modulRepository, DateProvider dateProvider) {
		this.modulGelerntEvent = modulGelerntEvent;
		this.modulRepository = modulRepository;
		this.dateProvider = dateProvider;
	}

	/**
	 * Saves a {@link ModulGelerntEvent} in the database.
	 * @param secondsLearned the time in seconds that the user learned the module
	 * @param modulId ID of the module
	 * @param username Username of the user
	 */
	public void saveEvent(int secondsLearned, String modulId, String username) {
		ModulGelerntEvent event = ModulGelerntEvent.initEvent(UUID.fromString(modulId), secondsLearned, username);
		if (event.enoughSecondsLearned()) {
			modulGelerntEvent.save(event);
			logger.info("Saved ModulGelerntEvent: {}", event);
		} else {
			throw new IllegalStateException("modul was not saved, because not enough seconds learned");
		}
	}

	/**
	 * Computes a {@code Map<LocalDate, List<ModulStat>} for the frontend to display how much of a module the user learned on a
	 * specific day.
	 * @param days The amount of recent days that should be computed
	 * @param username The username of the user
	 * @return A {@code Map<LocalDate, List<ModulStat>}
	 */
	public Map<LocalDate, List<ModulStat>> getStatisticsForRecentDays(int days, String username) {
		List<Modul> modules = modulRepository.findByUsername(username);
		Map<LocalDate, List<ModulStat>> dataMap = new TreeMap<>();

		for (int i = 0; i < days; i++) {
			LocalDate date = dateProvider.getTodayDate().minusDays(i);
			List<ModulStat> statistics = new LinkedList<>();
			for (Modul modul : modules) {
				int seconds = modulGelerntEvent.getSumSecondsLearned(date, username, modul.getFachId());
				if(seconds > 0) {
					ModulStat stat =  new ModulStat(modul.getName(), String.valueOf(seconds));
					statistics.add(stat);
				}
			}

			if (statistics.isEmpty()) continue;

			dataMap.put(date, statistics);
		}

		logger.info("Generated statistics for the last {} days for user '{}': {}", days, username, dataMap);
		return dataMap;
	}

	/**
	 * Computed the average study time a user studied per day
	 * @param username
	 * @return
	 */
	public Integer computeAverageStudyTimePerDay(String username) {
		List<ModulGelerntEvent> allEvents = modulGelerntEvent.getAllByUsername(username);
		int anzahlEvents = (int) allEvents.stream().map(ModulGelerntEvent::dateGelernt).distinct().count();
		int sum = allEvents.stream().mapToInt(ModulGelerntEvent::secondsLearned).sum();

		if(anzahlEvents == 0) return 0;

		logger.info("Computed average study time per day for user '{}': {} seconds over {} days", username, sum, anzahlEvents);
		return (int) Math.round((double) sum / anzahlEvents);
	}

	/**
	 * Deletes all {@link ModulGelerntEvent} in the database by their modulId
	 * @param modulId ID of the module whose events should be deleted
	 */
	public void deleteAllModulEvents(UUID modulId) {
		modulGelerntEvent.deleteAllByModulId(modulId);
	}
}
