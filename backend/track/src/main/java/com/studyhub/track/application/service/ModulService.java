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

	/**
	 * Löscht im gesamten System alle Datensätze die mit modulId assoziiert sind.
	 *
	 * @param fachId Fach-Id des zu löschenden Moduls
	 * @param username Username des Benutzers
	 * @return true, nachdem das Modul existierte und alle anderen Datensätze gelöscht wurden. false, wenn das Modul nicht existiert.
	 */
	@Transactional
	public boolean deleteModul(UUID fachId, String username) {
		int success = modulRepository.deleteByUuid(fachId);
		if (success == 0) {
			return false;
		}
		sessionService.deleteModuleFromBlocks(fachId, username);
		modulEventService.deleteAllModulEvents(fachId);
		log.warn("deleted modul with id:%s".formatted(fachId.toString()));
		return true;
	}

	/**
	 * Resets the seconds learned of a module to 0.
	 * @param fachId The id of the module to reset.
	 */
	@Transactional
	public void resetSecondsLearnedOfModul(UUID fachId) {
		Modul m = modulRepository.findByUuid(fachId);

		if(m == null) return;

		m.resetSecondsLearned();
		modulRepository.save(m);
		log.info("Resetted secondsLearned to 0 of modul with id: '%s'".formatted(fachId.toString()));
	}

	/**
	 * Finds all modules of a user by their active status.
	 * @param isActive True for active modules, false for inactive modules.
	 * @param username The username of the user.
	 * @return List of modules matching the criteria.
	 */
	public List<Modul> findActiveModuleByUsername(boolean isActive, String username) {
		return modulRepository.findActiveModuleByUsername(isActive, username);
	}

	/**
	 * Toggles the activity status of a module.
	 * @param fachId The id of the module to toggle.
	 */
	@Transactional
	public void toggleModulActivity(UUID fachId) {
		Modul m = modulRepository.findByUuid(fachId);
		m.toggleActivity();
		modulRepository.save(m);
	}

	/**
	 * Finds the seconds learned of a module by its id.
	 * @param fachId The id of the module.
	 * @return The seconds learned of the desired module.
	 */
	public int getSecondsForId(UUID fachId) {
		return modulRepository.findSecondsById(fachId);
	}

	/**
	 * Finds the name of a module by its id.
	 * @param fachId The id of the module.
	 * @return The name of the desired module.
	 */
	public String findModulNameByFachId(UUID fachId) {
		return modulRepository.findByUuid(fachId).getName();
	}

	/**
	 * Get total study time for a user.
	 * @param username The username of the user.
	 * @return Total study time in seconds.
	 */
	public Integer getTotalStudyTimeForUser(String username) {
		Integer totalStudyTime = modulRepository.getTotalStudyTime(username);

		if(totalStudyTime == null) {
			return 0;
		}
		return totalStudyTime;
	}

	/**
	 * Get total study time per semester for a user.
	 * @param username The username of the user.
	 * @return Map of fachsemester to total study time in seconds.
	 */
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

	/**
	 * Counts the number of active modules for a user.
	 * @param username The username of the user.
	 * @return Number of active modules.
	 */
	public Integer countActiveModules(String username) {
		return modulRepository.countActiveModules(username);
	}

	/**
	 * Counts the number of not active modules for a user.
	 * @param username The username of the user.
	 * @return Number of not active modules.
	 */
	public Integer countNotActiveModules(String username) {
		return modulRepository.countNotActiveModules(username);
	}

	/**
	 * Finds the module with the maximum seconds learned for a user.
	 * @param username The username of the user.
	 * @return Name of the module with maximum seconds learned.
	 */
	public String findModulWithMaxSeconds(String username) {
		return modulRepository.findByMaxSeconds(username);
	}

	/**
	 * Finds the module with the minimum seconds learned for a user.
	 * @param username The username of the user.
	 * @return Name of the module with minimum seconds learned.
	 */
	public String findModulWithMinSeconds(String username) {
		return modulRepository.findByMinSeconds(username);
	}

	/**
	 * Gets a map of module IDs to module names for a user.
	 * @param username The username of the user.
	 * @return Map of module ids to module names.
	 */
	public Map<UUID, String> getModuleMap(String username) {
		List<Modul> all = modulRepository.findByUsername(username);
		Map<UUID, String> map = new HashMap<>();

		for (Modul m: all) {
			map.put(m.getFachId(), m.getName());
		}

		return map;
	}

	/**
	 * Checks if the module database is healthy.
	 * @return True if healthy, false otherwise.
	 */
	public boolean isModulDbHealthy() {
		log.info("fetching database health status");
		return modulRepository.isModulDbHealthy();
	}

	/**
	 * Formats a date string to German format.
	 * @param zeitString Date string in "yyyy-MM-dd HH:mm:ss" format.
	 * @return Formatted date string in "dd.MM.yyyy, HH:mm" format.
	 */
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

	/**
	 * Finds a module by its fachId.
	 * @param uuid
	 * @return
	 */
	public Modul findByFachId(UUID uuid) {
		return modulRepository.findByUuid(uuid);
	}

	/**
	 * Finds all modules for a given username.
	 * @param username The username of the user.
	 * @return List of modules for the user.
	 */
	public List<Modul> findAllByUsername(String username) {
		return modulRepository.findByUsername(username);
	}

	/**
	 * Checks if a new module can be created for a user based on a limit.
	 * @param username The username of the user.
	 * @param limit The maximum number of modules allowed.
	 * @return True if a new module can be created, false otherwise.
	 */
	public boolean modulCanBeCreated(String username, int limit) {
		List<Modul> module = modulRepository.findByUsername(username);
        return module.size() < limit;
    }

	/**
	 * Gets all module terms for a given module ID.
	 * @param fachId The fachId of the module.
	 * @return List of module terms.
	 */
	public List<Modultermin> getModultermineByModulId(UUID fachId) {
		Modul m = findByFachId(fachId);
		if (m == null) {
			return Collections.emptyList();
		}
		return m.getModultermine();
	}

	/**
	 * Adds a new module term for a given module and saves it.
	 * @param req The request containing module term details.
	 */
	@Transactional
	public void saveNewModultermin(NeuerModulterminRequest req) {
		Modultermin termin = req.toModultermin();
		UUID modulId = req.getModulId();
		Modul modul = findByFachId(modulId);
		modul.putNewModulTermin(termin);
		modulRepository.save(modul);
	}

	/**
	 * Computes a map of fachsemester to list of modules for a user.
	 * @param username The username of the user.
	 * @return Map of fachsemester to list of modules.
	 */
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

	/**
	 * Gets general statistics for a user.
	 * @param username The username of the user.
	 * @return General statistics DTO.
	 */
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
}
