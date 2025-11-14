package com.studyhub.track.service;

import com.studyhub.track.application.service.LernplanAktivierungsService;
import com.studyhub.track.domain.model.lernplan.LernplanRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LernplanAktivierungsServiceTest {

    @Mock
    private LernplanRepository lernplanRepository;

    @InjectMocks
    private LernplanAktivierungsService service;

    @Test
    @DisplayName("Wenn ein Lernplan aktiviert werden soll, werden zunächst alle vorhandenen Lernpläne deaktiviert und dann der gewünschte Lernplan aktiviert")
    void test_1() {
        UUID desiredLernplanId = UUID.randomUUID();
        String username = "peter77";

        service.setActiveLernplan(desiredLernplanId, username);

        verify(lernplanRepository, times(1)).deactivateAllByUsername(username);
        verify(lernplanRepository, times(1)).setIsActiveOfLernplan(desiredLernplanId, true);
    }
}
