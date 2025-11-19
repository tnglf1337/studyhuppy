package com.studyhub.track.adapter.web.controller.api;

import com.studyhub.track.adapter.web.SemesterForm;
import com.studyhub.track.application.service.ModulService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Deprecated
@Controller
@RequestMapping("/api/modul/v1")
public class SemesterApiController {

	private ModulService modulService;

	public SemesterApiController(ModulService modulService) {
		this.modulService = modulService;
	}

	@GetMapping("/semester")
	public String semester(SemesterForm semesterForm) {
		return "semester";
	}

	@PostMapping("/create-semester")
	public String createSemester(@Valid @ModelAttribute SemesterForm semesterForm,
								 BindingResult bindingResult,
								 RedirectAttributes redirectAttributes,
	                             @CookieValue("auth_token") String token) {
		if (bindingResult.hasErrors()) return "semester";

		redirectAttributes.addFlashAttribute("semesterForm", semesterForm);

		modulService.saveAllNewModule(semesterForm.toModulList(token));
		return "redirect:/dashboard";
	}
}
