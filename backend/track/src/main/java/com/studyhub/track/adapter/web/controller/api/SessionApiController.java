package com.studyhub.track.adapter.web.controller.api;

import com.studyhub.track.adapter.web.controller.request.dto.SessionBeendetEventRequest;
import com.studyhub.track.adapter.web.controller.request.dto.SessionBewertungStatistikRequest;
import com.studyhub.track.adapter.web.controller.request.dto.SessionDeleteRequest;
import com.studyhub.track.application.service.SessionBewertungGeneralStatistikDto;
import com.studyhub.track.application.service.SessionBewertungService;
import com.studyhub.track.application.service.SessionEventsService;
import com.studyhub.track.application.service.dto.SessionBewertungAveragesDto;
import com.studyhub.track.application.service.dto.SessionInfoDto;
import com.studyhub.track.application.JWTService;
import com.studyhub.track.application.service.SessionService;
import com.studyhub.track.application.service.dto.SessionRequest;
import com.studyhub.track.domain.model.session.Session;
import com.studyhub.track.domain.service.SessionLoeschenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/session/v1")
public class SessionApiController {

	private final SessionService sessionService;
	private final SessionLoeschenService sessionLoeschenService;
	private final SessionEventsService sessionEventsService;
	private final SessionBewertungService sessionBewertungService;
	private final JWTService jwtService;

	public SessionApiController(SessionService sessionService, SessionLoeschenService sessionLoeschenService, SessionEventsService sessionEventsService, SessionBewertungService sessionBewertungService, JWTService jwtService) {
		this.sessionService = sessionService;
		this.sessionLoeschenService = sessionLoeschenService;
		this.sessionEventsService = sessionEventsService;
		this.sessionBewertungService = sessionBewertungService;
		this.jwtService = jwtService;
	}

	@GetMapping("/get-sessions")
	public ResponseEntity<List<Session>> getSessions(HttpServletRequest request) {
		String username = jwtService.extractUsernameFromHeader(request);
		List<Session> sessions = sessionService.getSessionsByUsername(username);

		if (sessions.isEmpty()) {
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.ok(sessions);
		}
	}

	@GetMapping("/get-session-by-id")
	public ResponseEntity<Session> getSessionById(@RequestParam UUID sessionId) {
		Session session = sessionService.getSessionByFachId(sessionId);
		return ResponseEntity.ok(session);
	}

	@GetMapping("/get-lernplan-session-data")
	public ResponseEntity<List<SessionInfoDto>> getLernplanSessionData(HttpServletRequest request) {
		String username = jwtService.extractUsernameFromHeader(request);
		List<SessionInfoDto> sessionInfo = sessionService.getLernplanSessionDataOfUser(username);

		if (sessionInfo.isEmpty()) {
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.ok(sessionInfo);
		}
	}

	@PostMapping("/create")
	public ResponseEntity<Void> createSession(@RequestBody SessionRequest sessionRequest, HttpServletRequest request) {
		String username = jwtService.extractUsernameFromHeader(request);
		boolean success = sessionService.save(sessionRequest.toEntity(username));

		if (success) {
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.internalServerError().build();
		}
	}

	@DeleteMapping("/delete-session")
	public ResponseEntity<Void> deleteSession(@RequestBody SessionDeleteRequest deleteRequest, HttpServletRequest request) {
		String username = jwtService.extractUsernameFromHeader(request);
		sessionLoeschenService.sessionLoeschen(deleteRequest.fachId(), username);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/has-lernsessions")
	public ResponseEntity<Boolean> hasLernSessions(HttpServletRequest request) {
		String username = jwtService.extractUsernameFromHeader(request);
		List<Session> data = sessionService.getSessionsByUsername(username);
		return ResponseEntity.ok(!data.isEmpty());
	}

	@PostMapping("/save-session-beendet-event" )
	public ResponseEntity<Void> saveSessionBeendetEvent(@RequestBody SessionBeendetEventRequest eventRequest, HttpServletRequest request) {
		String username = jwtService.extractUsernameFromHeader(request);
		boolean success = sessionEventsService.save(eventRequest.toEntity(username));
		if (success) {
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.internalServerError().build();
		}
	}

	@PostMapping("/edited-session")
	public ResponseEntity<Void> editedSessison(@RequestBody SessionRequest sessionRequest, HttpServletRequest request) {
		sessionService.saveEditedSession(sessionRequest);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/get-general-session-bewertung-statistik")
	public ResponseEntity<SessionBewertungGeneralStatistikDto> getGeneralSessionBewertungStatistik(HttpServletRequest request) {
		String username = jwtService.extractUsernameFromHeader(request);
		SessionBewertungGeneralStatistikDto generalStatistikDto = sessionBewertungService.getSessionBewertungStatistikByUsername(username);
		return ResponseEntity.ok(generalStatistikDto);
	}

	@GetMapping("/get-session-bewertung-statistik")
	public ResponseEntity<Map<LocalDate, SessionBewertungAveragesDto>> getSessionBewertungStatistik(
			SessionBewertungStatistikRequest sessionBewertungRequest)
	{
		Map<LocalDate, SessionBewertungAveragesDto> res = sessionBewertungService.getMonthlySessionBewertungStatistik(
				sessionBewertungRequest.sessionId()
		);
		return ResponseEntity.ok(res);
	}
}
