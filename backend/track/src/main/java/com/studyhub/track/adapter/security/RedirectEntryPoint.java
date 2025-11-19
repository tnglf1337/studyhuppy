package com.studyhub.track.adapter.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Deprecated
@Component
public class RedirectEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
		String redirectUrl = request.getRequestURL().toString();
		String loginUrl = "http://localhost:8084/login?redirect=" + URLEncoder.encode(redirectUrl, StandardCharsets.UTF_8.name());
		response.sendRedirect(loginUrl);
	}
}

