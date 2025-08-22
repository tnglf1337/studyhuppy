package com.studyhub.track.adapter.web.controller.api;

import com.studyhub.track.domain.model.session.Session;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/session/v1")
public class SessionApiController {

	@PostMapping("/create")
	public ResponseEntity<Void> createSession(@RequestBody Session session) {
		// Logic to create a session
		System.out.println("ping session create");
		System.out.println(session);
		return ResponseEntity.ok().build();
	}
}
