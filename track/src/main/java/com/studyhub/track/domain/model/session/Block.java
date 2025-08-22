package com.studyhub.track.domain.model.session;

import lombok.Data;
import java.util.UUID;

@Data
public class Block {
	private UUID id;
	private int lernzeitSeconds;
	private int pausezeitSeconds;
}
