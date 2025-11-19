package com.studyhub.track.application.service;

import com.studyhub.track.application.service.dto.SessionBewertungAveragesDto;
import com.studyhub.track.domain.model.session.SessionBeendetEvent;
import com.studyhub.track.domain.model.session.SessionBewertung;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SessionBewertungService {

	private final SessionBeendetEventRepository sessionBeendetEventRepository;

	public SessionBewertungService(SessionBeendetEventRepository sessionBeendetEventRepository) {
		this.sessionBeendetEventRepository = sessionBeendetEventRepository;
	}

	/**
	 * Calculates the average konzentrationBewertung for a given username.
	 * @param username The username for which to calculate the average Bewertung.
	 * @return The average konzentrationBewertung as a Double.
	 */
	public Double getAverageKonzentrationBewertungByUsername(String username) {
		return getAverageByUsername(username, SessionBewertung::getKonzentrationBewertung);
	}

	/**
	 * Calculates the average produktivitaetBewertung for a given username.
	 * @param username The username for which to calculate the average Bewertung.
	 * @return The average produktivitaetBewertung as a Double.
	 */
	public Double getAverageProduktivitaetBewertungByUsername(String username) {
		return getAverageByUsername(username, SessionBewertung::getProduktivitaetBewertung);
	}

	/**
	 * Calculates the average schwierigkeitBewertung for a given username.
	 * @param username The username for which to calculate the average Bewertung.
	 * @return The average schwierigkeitBewertung as a Double.
	 */
	public Double getAverageSchwierigkeitBewertungByUsername(String username) {
		return getAverageByUsername(username, SessionBewertung::getSchwierigkeitBewertung);
	}

	/**
	 * Generates a histogram of konzentrationBewertung for a given username.
	 * @param username The username of the user
	 * @return A map representing the histogram with 10 bins.
	 */
	public Map<Integer, Integer> getKonzentrationHisto(String username) {
		return getHistogramByUsername(username, SessionBewertung::getKonzentrationBewertung);
	}

	/**
	 * Generates a histogram of produktivitaetBewertung for a given username.
	 * @param username The username of the user
	 * @return A map representing the histogram with 10 bins.
	 */
	public Map<Integer, Integer> getProduktivitaetHisto(String username) {
		return getHistogramByUsername(username, SessionBewertung::getProduktivitaetBewertung);
	}

	/**
	 * Generates a histogram of schwierigkeitBewertung for a given username.
	 * @param username The username of the user
	 * @return A map representing the histogram with 10 bins.
	 */
	public Map<Integer, Integer> getSchwierigkeitHisto(String username) {
		return getHistogramByUsername(username, SessionBewertung::getSchwierigkeitBewertung);
	}

	/**
	 * Gets the overall statistics for all SessionBewertung attributes averaged in a dto.
	 * @param username The username for which to get the statistics.
	 * @return A SessionBewertungGeneralStatistikDto containing the average values.
	 */
	public SessionBewertungGeneralStatistikDto getSessionBewertungStatistikByUsername(String username) {
		return new SessionBewertungGeneralStatistikDto(
			getAverageKonzentrationBewertungByUsername(username),
			getAverageProduktivitaetBewertungByUsername(username),
			getAverageSchwierigkeitBewertungByUsername(username)
		);
	}

	/**
	 * Computes monthly averages of SessionBewertung for a given sessionId. Needed for the chart rendering in frontend.
	 * @param sessionId The id of the session
	 * @return A map where the key is the date and the value is a SessionBewertungAveragesDto containing average ratings for that date.
	 */
	public Map<LocalDate, SessionBewertungAveragesDto> getMonthlySessionBewertungStatistik(UUID sessionId) {
		List<SessionBeendetEvent> events = sessionBeendetEventRepository.findAllBySessionId(sessionId);

		Map<LocalDate, List<SessionBeendetEvent>> grouped = events.stream()
				.collect(Collectors.groupingBy(e -> e.getBeendetDatum().toLocalDate()));

		Map<LocalDate, SessionBewertungAveragesDto> groupedAverages = grouped.entrySet().stream()
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						entry -> {
							List<SessionBeendetEvent> eventsList = entry.getValue();

							double avgKonz = eventsList.stream()
									.mapToDouble(e -> e.getBewertung().getKonzentrationBewertung())
									.average()
									.orElse(0.0);

							double avgProduktiv = eventsList.stream()
									.mapToDouble(e -> e.getBewertung().getProduktivitaetBewertung())
									.average()
									.orElse(0.0);

							double avgSchwierigkeit = eventsList.stream()
									.mapToDouble(e -> e.getBewertung().getSchwierigkeitBewertung())
									.average()
									.orElse(0.0);

							SessionBewertungAveragesDto averagesDto = new SessionBewertungAveragesDto(avgKonz, avgProduktiv, avgSchwierigkeit);

							return averagesDto;
						}
				));
		return groupedAverages;
	}

	/**
	 * Helper method to calculate average of a specific SessionBewertung attribute by username.
	 * @param username Username of the user
	 * @param mapper Function to map attributes of the SessionBewertung
	 * @return
	 */
	private Double getAverageByUsername(
			String username,
			Function<SessionBewertung, Integer> mapper) {

		List<SessionBeendetEvent> events = sessionBeendetEventRepository.findAllByUsername(username);

		if (events.isEmpty()) return 0.0;

		int sum = events.stream()
				.map(SessionBeendetEvent::getBewertung)
				.map(mapper)
				.reduce(0, Integer::sum);

		return sum / (double) events.size();
	}

	/**
	 * Helper method to create a histogram of a specific SessionBewertung attribute by username.
	 * @param username Username of the user
	 * @param mapper Function to map attributes of the SessionBewertung
	 * @return A map representing the histogram with 10 bins.
	 */
	private Map<Integer, Integer> getHistogramByUsername(String username,
	                                                     Function<SessionBewertung, Integer> mapper) {
		List<SessionBeendetEvent> events = sessionBeendetEventRepository.findAllByUsername(username);
		Map<Integer, Integer> histogram = new HashMap<>();

		for (int i = 0; i <= 10; i++) histogram.put(i, 0);

		for (int i = 0; i <= 10; i++) {
			int j = i;
			events.stream()
					.map(SessionBeendetEvent::getBewertung)
					.map(mapper)
					.filter(bewertung -> bewertung == j)
					.forEach(bewertung -> histogram.put(j, histogram.get(j) + 1));
		}

		return histogram;
	}
}