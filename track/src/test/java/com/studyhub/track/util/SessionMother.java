package com.studyhub.track.util;

import com.studyhub.track.domain.model.session.Block;
import com.studyhub.track.domain.model.session.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SessionMother {

	public static Session createSessionWithNRandomBlocks(UUID sessionId, int n) {
		List<Block> blocks = new ArrayList<>();

		for(int i = 1; i < n+1; i++) {
			blocks.add(new Block(UUID.randomUUID(), UUID.randomUUID(), "Modul " + i, 1000*i, 2000*i));
		}

		return new Session(
				sessionId,
				"Test Session",
				"Test Description",
				"Test Location",
				blocks
		);
	}

	public static Session createSessionWithNRandomBlocks(int n) {
		List<Block> blocks = new ArrayList<>();

		for(int i = 1; i < n+1; i++) {
			blocks.add(new Block(UUID.randomUUID(), UUID.randomUUID(), "Modul " + i,1000*i, 2000*i));
		}

		return new Session(
				UUID.randomUUID(),
				"Test Session",
				"Test Description",
				"Test Location",
				blocks
		);
	}
}
