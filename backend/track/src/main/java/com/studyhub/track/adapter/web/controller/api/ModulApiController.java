package com.studyhub.track.adapter.web.controller.api;

import com.studyhub.track.adapter.authentication.AuthenticationService;
import com.studyhub.track.adapter.metric.PrometheusMetrics;
import com.studyhub.track.adapter.web.controller.request.dto.AddSecondsRequest;
import com.studyhub.track.adapter.web.controller.request.dto.TimerRequest;
import com.studyhub.track.application.JWTService;
import com.studyhub.track.adapter.db.modul.ModulDto;
import com.studyhub.track.adapter.db.modul.ModulMapper;
import com.studyhub.track.adapter.web.*;
import com.studyhub.track.application.service.ModulEventService;
import com.studyhub.track.application.service.ModulService;
import com.studyhub.track.application.service.ModulUpdateService;
import com.studyhub.track.application.service.dto.ModulSelectDto;
import com.studyhub.track.application.service.dto.NeuerModulterminRequest;
import com.studyhub.track.domain.model.modul.Modul;
import com.studyhub.track.domain.model.modul.Modultermin;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/modul/v1")
public class ModulApiController {
	private final ModulService modulService;
	private final ModulUpdateService modulUpdateService;
	private final ModulEventService modulEventService;
	private final AuthenticationService authenticationService;
	private final JWTService jwtService;
	private final PrometheusMetrics metrics;

	public ModulApiController(ModulService service, ModulUpdateService modulUpdateService, ModulEventService modulEventService, AuthenticationService authenticationService , JWTService jwtService, PrometheusMetrics metrics) {
		this.modulService = service;
		this.modulUpdateService = modulUpdateService;
		this.modulEventService = modulEventService;
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
        this.metrics = metrics;
    }

	/** GET MAPPINGS **/
	@GetMapping("/get-seconds")
	public ResponseEntity<Integer> getSeconds(@RequestParam("fachId") String fachId) {
		return ResponseEntity.ok(modulService.findSecondsById(UUID.fromString(fachId)));
	}

	@GetMapping("/module-map")
	public ResponseEntity<Map<UUID, String>> getModuleMap(HttpServletRequest req) {
		String username = jwtService.extractUsernameFromHeader(req);
		return ResponseEntity.ok(modulService.getModuleMap(username));
	}

	@GetMapping("/module-name")
	public ResponseEntity<String> getModulName(String modulFachId) {
		String modulName = modulService.findModulNameByFachId(UUID.fromString(modulFachId));
		return ResponseEntity.ok(modulName);
	}

	@GetMapping("/get-all-module-by-fachsemester")
	public ResponseEntity<Map<Integer, List<Modul>>> getAllModuleByFachsemester(HttpServletRequest request) {
		String username = jwtService.extractUsernameFromHeader(request);
		Map<Integer, List<Modul>> resultMap = modulService.getFachsemesterModuleMap(username);
		return ResponseEntity.ok(resultMap);
	}

	@GetMapping("/get-modul-select-data")
	public ResponseEntity<List<ModulSelectDto>> getModulSelectData(HttpServletRequest request) {
		String username = jwtService.extractUsernameFromHeader(request);
		List<ModulSelectDto> data = modulService.getModulSelectData(username);
		return ResponseEntity.ok(data);
	}

	@GetMapping("/has-module")
	public ResponseEntity<Boolean> hasModule(HttpServletRequest request) {
		String username = jwtService.extractUsernameFromHeader(request);
		List<Modul> data = modulService.findAllByUsername(username);
		return ResponseEntity.ok(!data.isEmpty());
	}

	@GetMapping("/get-aktivitaet-status/{fachId}")
	public ResponseEntity<Boolean> getAktivitaetStatus(@PathVariable("fachId") String fachId) {
		boolean isActive = modulService.findByFachId(UUID.fromString(fachId)).isActive();
		return ResponseEntity.ok(isActive);
	}

	@GetMapping("/getModultermine")
	public ResponseEntity<List<Modultermin>> getModultermine(@RequestParam("modulId") UUID modulId) {
		return ResponseEntity.ok(modulService.getModultermineByModulId(modulId));
	}

	@GetMapping("/get-all-by-username")
	public ResponseEntity<List<ModulDto>> getAllByUsername(HttpServletRequest request) {
		String username = jwtService.extractUsernameFromHeader(request);
		List<ModulDto> l = modulService.findAllByUsername(username).stream().map(ModulMapper::toModulDtoNoId).toList();
		return ResponseEntity.ok(l);
	}

	@GetMapping("/get-active-modules")
	public ResponseEntity<List<Modul>> getActiveModules(HttpServletRequest request) {
		String username = jwtService.extractUsernameFromHeader(request);
		List<Modul> l = modulService.findActiveModuleByUsername(true, username);
		return ResponseEntity.ok(l);
	}

	/** POST MAPPINGS **/
	@PostMapping("/new-modul")
	public ResponseEntity<Void> newModule(@RequestBody ModulForm modulForm, HttpServletRequest request) {
		String username = jwtService.extractUsernameFromHeader(request);
		int semester = authenticationService.getSemesterOfUser(username, jwtService.extractTokenFromHeader(request.getHeader("Authorization")));
		Modul modul = modulForm.newModulFromFormData(username, semester);
		modulService.saveNewModul(modul);
		return ResponseEntity.status(HttpStatus.CREATED).build();

	}

	@PutMapping("/change-active")
	public ResponseEntity<Void> activate(@RequestParam("fachId")  String fachId) {
		modulService.toggleModulActivity(UUID.fromString(fachId));
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@PostMapping("/addModultermin")
	public ResponseEntity<Void> addModultermin(@RequestBody NeuerModulterminRequest req) {
		modulService.saveNewModultermin(req);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping("/update")
	public ResponseEntity<Void> updateSeconds(@RequestBody TimerRequest timerRequest, HttpServletRequest httpServletRequest) {
		try {
			String username = jwtService.extractUsernameFromHeader(httpServletRequest);
			UUID modulId = UUID.fromString(timerRequest.modulId());
			int secondsLearned = timerRequest.toSeconds();
			modulUpdateService.updateSeconds(modulId, secondsLearned);
			modulEventService.saveEvent(secondsLearned, modulId, username);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		metrics.incrementTotalRequests();
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@PostMapping("/add-seconds")
	public ResponseEntity<Void> addSeconds(@RequestBody AddSecondsRequest request, HttpServletRequest httpServletRequest) {
		String username = jwtService.extractUsernameFromHeader(httpServletRequest);
		UUID modulId = request.modulId();
		int secondsLearned = request.localTimeToSeconds();
		if(secondsLearned > 5) {
			modulUpdateService.updateSeconds(modulId, secondsLearned);
			modulEventService.saveEvent(secondsLearned, modulId, username);
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	/** PUT MAPPINGS **/
	@PutMapping("/reset")
	public ResponseEntity<Void> reset(@RequestParam("fachId") String fachId) {
		try {
			modulService.resetSecondsLearnedOfModul(UUID.fromString(fachId));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	/** DELETE MAPPINGS **/
	@DeleteMapping("/delete")
	public ResponseEntity<Void> deleteModul(@RequestParam UUID fachId, HttpServletRequest httpServletRequest) {
		String username = jwtService.extractUsernameFromHeader(httpServletRequest);
		boolean success = modulService.deleteModul(fachId, username);

		if(success) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

}
