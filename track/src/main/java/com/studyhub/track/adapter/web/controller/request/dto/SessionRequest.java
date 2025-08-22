package com.studyhub.track.adapter.web.controller.request.dto;

import com.studyhub.track.domain.model.session.Block;
import com.studyhub.track.domain.model.session.Session;

import java.util.List;
import java.util.UUID;

public record SessionRequest(
		String fachId,
		String titel,
		String beschreibung,
		List<Block> blocks
) {

	public Session toEntity(String username) {
		UUID newFachId = UUID.randomUUID();

		for(Block block : blocks) {
			block.setFachId(UUID.randomUUID());
		}

		return new Session(
				newFachId,
				username,
				titel,
				beschreibung,
				blocks
		);
	}

}
