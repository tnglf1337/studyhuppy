package com.studyhub.track.adapter.web.controller.api;

import com.studyhub.track.adapter.authentication.AuthenticationService;
import com.studyhub.track.adapter.metric.PrometheusMetrics;
import com.studyhub.track.adapter.web.controller.request.dto.AddSecondsRequest;
import com.studyhub.track.application.JWTService;
import com.studyhub.track.adapter.db.modul.ModulDto;
import com.studyhub.track.adapter.db.modul.ModulMapper;
import com.studyhub.track.adapter.mail.KlausurReminderDto;
import com.studyhub.track.adapter.mail.KlausurReminderService;
import com.studyhub.track.adapter.web.*;
import com.studyhub.track.adapter.web.controller.request.dto.AddTimeRequest;
import com.studyhub.track.application.service.ModulEventService;
import com.studyhub.track.application.service.ModulService;
import com.studyhub.track.application.service.dto.ModulUpdateRequest;
import com.studyhub.track.application.service.dto.NeuerModulterminRequest;
import com.studyhub.track.domain.model.modul.Modul;
import com.studyhub.track.domain.model.modul.Modultermin;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/modul/v1")
public class ModulApiController {

	@Value("${maxModule}")
	private int MAX_MODULE;

	private final ModulService modulService;
	private final ModulEventService modulEventService;
	private final AuthenticationService authenticationService;
	private final KlausurReminderService klausurReminderService;
	private final JWTService jwtService;
	private final PrometheusMetrics metrics;

	public ModulApiController(ModulService service, ModulEventService modulEventService, AuthenticationService authenticationService, KlausurReminderService klausurReminderService, JWTService jwtService, PrometheusMetrics metrics) {
		this.modulService = service;
		this.modulEventService = modulEventService;
        this.authenticationService = authenticationService;
        this.klausurReminderService = klausurReminderService;
		this.jwtService = jwtService;
        this.metrics = metrics;
    }

	@AngularApi
	@PostMapping("/update")
	public ResponseEntity<Void> updateSeconds(@RequestBody ModulUpdateRequest request) {
		try {
			modulService.updateSeconds(UUID.fromString(request.fachId()), request.secondsLearned());
			modulEventService.saveEvent(request, "");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		metrics.incrementTotalRequests();
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@AngularApi
	@PostMapping("/add-seconds")
	public ResponseEntity<Void> addSeconds(@RequestBody AddSecondsRequest request) {
		System.out.println("blub");
		modulService.addSecondsToModul(request.modulId(), request.secondsToAdd());
		return ResponseEntity.ok().build();
	}

	@AngularApi
	@GetMapping("/get-seconds")
	public ResponseEntity<Integer> getSeconds(@RequestParam("fachId") String fachId) {
		return ResponseEntity.ok(modulService.getSecondsForId(UUID.fromString(fachId)));
	}

	@AngularApi
	@GetMapping("/module-map")
	public ResponseEntity<Map<UUID, String>> getModuleMap(HttpServletRequest req) {
		String username = jwtService.extractUsernameFromHeader(req);
		return ResponseEntity.ok(modulService.getModuleMap(username));
	}

	@AngularApi
	@GetMapping("/module-name")
	public String getModulName(String modulFachId) {
		return modulService.findModulNameByFachid(UUID.fromString(modulFachId));
	}

	@Api
	@PostMapping("/data-klausur-reminding")
	public ResponseEntity<List<KlausurReminderDto>> findModuleWithoutKlausurDate(@RequestBody List<String> users) {
		return ResponseEntity.ok(klausurReminderService.findModuleWithoutKlausurDate(users));
	}

	@AngularApi
	@PutMapping("/reset")
	public ResponseEntity<Void> reset(@RequestParam("fachId") String fachId) {
		try {
			modulService.resetModulTime(UUID.fromString(fachId));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.OK).build();
	}

	@AngularApi
	@DeleteMapping("/delete")
	public ResponseEntity<Void> deleteModul(@RequestParam String fachId) {
		modulService.deleteModul(UUID.fromString(fachId));
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@AngularApi
	@GetMapping("/get-active-modules")
	public ResponseEntity<List<Modul>> getActiveModules(HttpServletRequest request) {
		String username = jwtService.extractUsernameFromHeader(request);
		List<Modul> l = modulService.findActiveModuleByUsername(true, username);
		return ResponseEntity.ok(l);
	}

	@AngularApi
	@PostMapping("/new-modul")
	public ResponseEntity<Void> newModule(@RequestBody ModulForm modulForm, HttpServletRequest request) {
		String username = jwtService.extractUsernameFromHeader(request);
		int semester = authenticationService.getSemesterOfUser(username, jwtService.extractTokenFromHeader(request.getHeader("Authorization")));

		Modul modul = modulForm.newModulFromFormData(modulForm, username, semester);
		/**
		 if (Optional.ofNullable(modulForm.stapelCheckbox()).orElse(false)) {
		 CreateNewStapelRequest request = new CreateNewStapelRequest(modul.getFachId().toString(), modulForm.stapelName(), modulForm.beschreibung(), modulForm.lernstufen(), jwtService.extractUsername(token));
		 request.validateForBindingResult(bindingResult);
		 if(bindingResult.hasErrors()) return "add-module";
		 stapelRequestService.sendCreateNewStapelRequest(request);
		 }
		 **/

		if (modulService.modulCanBeCreated(username, MAX_MODULE)) {
			modulService.saveNewModul(modul);
			return ResponseEntity.status(HttpStatus.CREATED).build();
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@AngularApi
	@GetMapping("/get-all-by-username")
	public ResponseEntity<List<ModulDto>> getAllByUsername(HttpServletRequest request) {
		String username = jwtService.extractUsernameFromHeader(request);
		List<ModulDto> l = modulService.findAllByUsername(username).stream().map(ModulMapper::toModulDtoNoId).toList();
		return ResponseEntity.ok(l);
	}

	@AngularApi
	@PutMapping("/change-active")
	public ResponseEntity<Void> activate(@RequestParam("fachId")  String fachId) {
		modulService.changeActivity(UUID.fromString(fachId));
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@AngularApi
	@PostMapping("/add-time")
	public ResponseEntity<Void> addTime(@RequestBody AddTimeRequest req) {
		try {
			modulService.addTime(UUID.fromString(req.getFachId()), req.getTime());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@AngularApi
	@GetMapping("/getModultermine")
	public ResponseEntity<List<Modultermin>> getModultermine(@RequestParam("modulId") UUID modulId) {
		return ResponseEntity.ok(modulService.getModultermineByModulId(modulId));
	}

	@AngularApi
	@PostMapping("/addModultermin")
	public ResponseEntity<Void> addModultermin(@RequestBody NeuerModulterminRequest req) {
		modulService.saveNewModultermin(req);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@AngularApi
	@GetMapping("/get-active-module2")
	public ResponseEntity<Map<Integer, List<Modul>>> getActiveModule2(HttpServletRequest request) {
		String username = jwtService.extractUsernameFromHeader(request);
		Map<Integer, List<Modul>> resultMap = modulService.getFachsemesterModuleMap(username);
		return ResponseEntity.ok(resultMap);
	}

	@AngularApi
	@GetMapping("/get-modul-select-data")
	public ResponseEntity<List<ModulSelectDto>> getModulSelectData(HttpServletRequest request) {
		String username = jwtService.extractUsernameFromHeader(request);
		List<ModulSelectDto> data = modulService.getModulSelectData(username);
		return ResponseEntity.ok(data);
	}
}
