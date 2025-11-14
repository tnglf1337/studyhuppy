package com.studyhub.track.application.service;


import com.studyhub.track.application.service.dto.GeneralStatisticsDto;
import com.studyhub.track.application.service.dto.GeneralStatisticsDtoBuilder;
import com.studyhub.track.application.service.dto.ModulSelectDto;
import com.studyhub.track.application.service.dto.NeuerModulterminRequest;
import com.studyhub.track.domain.model.modul.Modul;
import com.studyhub.track.domain.model.modul.Modultermin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.IntStream;

@Service
public class ModulService {
	private final Logger log = LoggerFactory.getLogger(ModulService.class);
	private final ModulRepository modulRepository;
	private final SessionService sessionService;
	private final ModulEventService modulEventService;

	public ModulService(ModulRepository modulRepository, SessionService sessionService, ModulEventService modulEventService) {
		this.modulRepository = modulRepository;
		this.sessionService = sessionService;
		this.modulEventService = modulEventService;
	}

	public void saveNewModul(Modul modul) {
		modulRepository.save(modul);
		log.info("Saved new modul '%s'".formatted(modul.getName()));
	}

	public void saveAllNewModule(List<Modul> modulList) {
		modulRepository.saveAll(modulList);
	}

	public List<Modul> findAll() {
		return modulRepository.findAll();
	}

	public void updateSeconds(UUID fachId, int seconds) throws ModulSecondsUpdateException {
		int res = modulRepository.updateSecondsByUuid(fachId, seconds);
		if (res == 0) throw new ModulSecondsUpdateException("could not update seconds for modul with id: %s".formatted(fachId.toString()));
		log.info("updated modul with id:%s to seconds=%s".formatted(fachId.toString(), String.valueOf(seconds)));
	}

	/**
	 * Löscht im gesamten System alle Datensätze die mit modulId assoziiert sind.
	 *
	 * @param modelId Fach-Id des zu löschenden Moduls
	 * @param username Username des Benutzers
	 * @return true, nachdem das Modul existierte und alle anderen Datensätze gelöscht wurden. false, wenn das Modul nicht existiert.
	 */
	@Transactional
	public boolean deleteModul(UUID modelId, String username) {
		int success = modulRepository.deleteByUuid(modelId);
		if (success == 0) {
			return false;
		}
		sessionService.deleteModuleFromBlocks(modelId, username);
		modulEventService.deleteAllModulEvents(modelId);
		log.warn("deleted modul with id:%s".formatted(modelId.toString()));
		return true;
	}

	public void resetSecondsLearnedOfModul(UUID modulId) {
		Modul m = modulRepository.findByUuid(modulId);

		if(m == null) return;

		m.resetSecondsLearned();
		modulRepository.save(m);
		log.info("resetted secondsLearned to 0 of modul with id: '%s'".formatted(modulId.toString()));
	}

	public List<Modul> findActiveModuleByUsername(boolean active, String username) {
		return modulRepository.findActiveModuleByUsername(active, username);
	}

	public void changeActivity(UUID fachId) {
		Modul m = modulRepository.findByUuid(fachId);
		m.changeActivity();
		modulRepository.save(m);
	}

	public void deactivateModul(UUID fachId) {
		modulRepository.setActive(fachId, false);
		log.info("deactivated modul id:%s".formatted(fachId.toString()));
	}

	public void addTime(UUID fachId, String time) throws ModulSecondsUpdateException {
		TimeConverter tc = new TimeConverter();
		int addSeconds = tc.timeToSeconds(time);
		int alreadyLearned = modulRepository.findSecondsById(fachId);
		int newSeconds = addSeconds + alreadyLearned;
		updateSeconds(fachId, newSeconds);
	}

	public int getSecondsForId(UUID fachId) {
		return modulRepository.findSecondsById(fachId);
	}

	public String findModulNameByFachid(UUID fachId) {
		return modulRepository.findByUuid(fachId).getName();
	}

	public Integer getTotalStudyTimeForUser(String username) {
		Integer totalStudyTime = modulRepository.getTotalStudyTime(username);

		if(totalStudyTime == null) {
			return 0;
		}
		return totalStudyTime;
	}

	public Map<Integer, Integer> getTotalStudyTimePerFachSemester(String username) {
		List<Modul> allModule = modulRepository.findByUsername(username);
		Set<Integer> fachsemesterMap = new HashSet<>();
		Map<Integer, Integer> res = new HashMap<>();

		for (Modul modul : allModule) {
			int fachsemester = modul.getSemesterstufe();
			fachsemesterMap.add(fachsemester);
		}

		for (Integer thisFachsemester : fachsemesterMap) {
			List<Modul> moduleWithThisFachsemester = allModule.stream()
					.filter(e -> Objects.equals(e.getSemesterstufe(), thisFachsemester))
					.toList();

			int sumSecondsLearned = moduleWithThisFachsemester.stream()
					.flatMapToInt(e -> IntStream.of(e.getSecondsLearned()))
					.sum();

			res.put(thisFachsemester, sumSecondsLearned);
		}

		return res;
	}

	public Integer countActiveModules(String username) {
		return modulRepository.countActiveModules(username);
	}

	public Integer countNotActiveModules(String username) {
		return modulRepository.countNotActiveModules(username);
	}

	public String findModulWithMaxSeconds(String username) {
		return modulRepository.findByMaxSeconds(username);
	}

	public String findModulWithMinSeconds(String username) {
		return modulRepository.findByMinSeconds(username);
	}

	public Map<UUID, String> getModuleMap(String username) {
		List<Modul> all = modulRepository.findByUsername(username);
		Map<UUID, String> map = new HashMap<>();

		for (Modul m: all) {
			map.put(m.getFachId(), m.getName());
		}

		return map;
	}

	public boolean isModulDbHealthy() {
		log.info("fetching database health status");
		return modulRepository.isModulDbHealthy();
	}


	public String dateStringGer(String zeitString) {
		try {
			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime dateTime = LocalDateTime.parse(zeitString, inputFormatter);

			DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")
					.withLocale(Locale.GERMANY);
			return dateTime.format(outputFormatter);
		} catch (Exception e) {
			return "Ungültiges Datums-/Zeitformat";
		}
	}

	public Modul findByFachId(UUID uuid) {
		return modulRepository.findByUuid(uuid);
	}

	public List<Modul> findAllByUsername(String username) {
		return modulRepository.findByUsername(username);
	}

	public boolean modulCanBeCreated(String username, int limit) {
		List<Modul> module = modulRepository.findByUsername(username);
        return module.size() < limit;
    }

	public List<Modultermin> getModultermineByModulId(UUID modulId) {
		Modul m = findByFachId(modulId);
		if (m == null) {
			return Collections.emptyList();
		}
		return m.getModultermine();
	}

	public void saveNewModultermin(NeuerModulterminRequest req) {
		Modultermin termin = req.toModultermin();
		UUID modulId = req.getModulId();
		Modul modul = findByFachId(modulId);
		modul.putNewModulTermin(termin);
		modulRepository.save(modul);
	}

	public Map<Integer, List<Modul>> getFachsemesterModuleMap(String username) {
		List<Modul> module = modulRepository.findActiveModuleByUsername(true, username);
		Map<Integer, List<Modul>> moduleMap = new TreeMap<>(Comparator.reverseOrder());
		Set<Integer> fachsemester = new HashSet<>();

		for (Modul m : module) {
			fachsemester.add(m.getSemesterstufe());
		}

		for (Integer fachsem : fachsemester) {
			List<Modul> listForMap = module.stream().filter( e -> e.getSemesterstufe() == fachsem).toList();
			moduleMap.put(fachsem, listForMap);
		}

		return moduleMap;
	}

	public List<ModulSelectDto> getModulSelectData(String username) {
		List<ModulSelectDto> res = new LinkedList<>();
		List<Modul> module = modulRepository.findByUsername(username);
		for (Modul m : module) {
			res.add(new ModulSelectDto(m.getFachId(), m.getName()));
		}
		return res;
	}

	public void addSecondsToModul(UUID uuid, LocalTime time, String username) {
		try {
			int seconds = localTimeToSeconds(time);
			updateSeconds(uuid, seconds);
			modulEventService.saveEvent(seconds, uuid.toString(), username);
		} catch(Exception e) {
			log.error("Error saving event", e);
		}
	}

	public void addSecondsToModul(UUID uuid, int seconds, String username) {
		try {
			updateSeconds(uuid, seconds);
			modulEventService.saveEvent(seconds, uuid.toString(), username);
		} catch(Exception e) {
			log.error("Error saving event", e);
		}
	}

	public GeneralStatisticsDto getGeneralStatistics(String username) {
		GeneralStatisticsDtoBuilder statBuilder = new GeneralStatisticsDtoBuilder().builder();

		statBuilder.withTotalStudyTime(getTotalStudyTimeForUser(username));
		statBuilder.withTotalStudyTimePerSemester(getTotalStudyTimePerFachSemester(username));
		statBuilder.withDurchschnittlicheLernzeitProTag(modulEventService.computeAverageStudyTimePerDay(username));
		statBuilder.withNumberActiveModules(countActiveModules(username));

		int numberActiveModules = countActiveModules(username);
		statBuilder.withNumberActiveModules(numberActiveModules);
		int numberNotActiveModules = countNotActiveModules(username);
		statBuilder.withNumberNotActiveModules(numberNotActiveModules);

		if(numberActiveModules == 0 && numberNotActiveModules == 0) {
			statBuilder.withMaxStudiedModul("Keine Module verfügbar");
			statBuilder.withMinStudiedModul("Keine Module verfügbar");
		} else {
			statBuilder.withMaxStudiedModul(findModulWithMaxSeconds(username));
			statBuilder.withMinStudiedModul(findModulWithMinSeconds(username));
		}

		return statBuilder.build();
	}

	private Integer localTimeToSeconds(LocalTime time) {
		return time.getHour() * 3600 + time.getMinute() * 60;
	}
}
