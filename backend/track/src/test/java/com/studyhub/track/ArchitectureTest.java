package com.studyhub.track;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import java.io.PrintStream;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;

@AnalyzeClasses(packagesOf = TrackApplication.class)
class ArchitectureTest {

	JavaClasses classesImport = new ClassFileImporter()
			.withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
			.importPackages("com.studyhub.track");

	@DisplayName("Die Onion-Architektur wird nicht verletzt.")
	@Test
	void testOnionArchitecture() {
		ArchRule rule = onionArchitecture()
				.domainModels("..domain.model..")
				.domainServices("..domain.service..")
				.applicationServices("..application.service..")
				.adapter("adapter", "..adapter..");
		rule.check(classesImport);
	}

	@DisplayName("Keine Klasse benutzt Field-injection.")
	@Test
	void noFieldInjection() {
		ArchRule rule = noFields().should().beAnnotatedWith(Autowired.class);
		rule.check(classesImport);
	}

	@DisplayName("Service-Klassen haben den Postfix Service")
	@Test
	void checkServicePostfix() {
		ArchRule rule = classes().that().areAnnotatedWith(Service.class).should().haveSimpleNameEndingWith("Service");
		rule.check(classesImport);
	}

	@DisplayName("Controller-Klassen haben den Postfix Controller")
	@Test
	void checkControllerPostfix() {
		ArchRule rule = classes().that().areAnnotatedWith(Controller.class).should().haveSimpleNameEndingWith("Controller");
		rule.check(classesImport);
	}

	@DisplayName("Handler-Methoden in Rest-Controller geben eine Response-Entity zurück")
	@Test
	void apiMethodsReturnResponseEntities() {
		ArchRule rule = methods().that().areDeclaredInClassesThat().areAnnotatedWith(RestController.class).should().haveRawReturnType(ResponseEntity.class);
		rule.check(classesImport);
	}

	@DisplayName("Controller-Klassen benutzen nur Klassen, die mit @Service annotiert sind")
	@Test
	void controllerOnlyAccessServices() {
		ArchRule rule = classes().that().areAnnotatedWith(Controller.class).should().dependOnClassesThat().areAnnotatedWith(Service.class);
		rule.check(classesImport);
	}

	@DisplayName("Keine Klasse benutzt System.out.println")
	@Test
	void noClassShouldUseSystemOutPrintln() {
		ArchRule rule = ArchRuleDefinition.noClasses()
				.should().callMethod(PrintStream.class, "println", String.class)
				.orShould().callMethod(PrintStream.class, "println", Object.class)
				.orShould().callMethod(PrintStream.class, "println", int.class)
				.orShould().callMethod(PrintStream.class, "println", long.class)
				.orShould().callMethod(PrintStream.class, "println", double.class)
				.orShould().callMethod(PrintStream.class, "println");
		rule.check(classesImport);
	}
}