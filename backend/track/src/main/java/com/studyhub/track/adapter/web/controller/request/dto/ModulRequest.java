package com.studyhub.track.adapter.web.controller.request.dto;

public record ModulRequest(
		String modulName,
		Integer creditPoints,
		Integer kontaktzeitStunden,
		Integer selbststudiumStunden
) {
}
