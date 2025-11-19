package com.studyhub.track.adapter.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Deprecated
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KlausurDateRequest {
	private String fachId;
	private LocalDate date;
	private String time;
}
