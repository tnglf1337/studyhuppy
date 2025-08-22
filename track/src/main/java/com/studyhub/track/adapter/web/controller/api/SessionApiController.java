package com.studyhub.track.adapter.web.controller.api;

import com.studyhub.track.adapter.web.controller.request.dto.SessionRequest;
import com.studyhub.track.application.JWTService;
import com.studyhub.track.application.service.SessionService;
import com.studyhub.track.domain.model.session.Session;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/session/v1")
public class SessionApiController {

	private final SessionService sessionService;
	private final JWTService jwtService;

	public SessionApiController(SessionService sessionService, JWTService jwtService) {
		this.sessionService = sessionService;
		this.jwtService = jwtService;
	}

	@PostMapping("/create")
	public ResponseEntity<Void> createSession(@RequestBody SessionRequest sessionRequest, HttpServletRequest request) {
		System.out.println("ping session create");
		String username = jwtService.extractUsernameFromHeader(request);
		boolean success = sessionService.save(sessionRequest.toEntity(username));

		if (success) {
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.internalServerError().build();
		}
	}
}
