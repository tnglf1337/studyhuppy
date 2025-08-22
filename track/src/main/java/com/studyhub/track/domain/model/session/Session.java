package com.studyhub.track.domain.model.session;

import java.util.List;
import java.util.UUID;

public class Session {
	private UUID id;
	private String titel;
	private String beschreibung;
	private List<Block> blocks;
}
