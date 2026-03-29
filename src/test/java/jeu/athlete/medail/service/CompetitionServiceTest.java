package jeu.athlete.medail.service;

import jeu.athlete.medail.domain.Competition;
import jeu.athlete.medail.dto.CompetitionRequestDTO;
import jeu.athlete.medail.dto.CompetitionResponseDTO;
import jeu.athlete.medail.exception.ResourceNotFoundException;
import jeu.athlete.medail.repository.CompetitionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompetitionServiceTest {

    @Mock
    private CompetitionRepository competitionRepository;

    @InjectMocks
    private CompetitionService competitionService;

    private Competition competition;
    private CompetitionRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        competition = Competition.builder()
                .id(1L)
                .nom("100m")
                .discipline("Athlétisme")
                .dateDebut(LocalDate.now())
                .dateFin(LocalDate.now())
                .statut("A_VENIR")
                .build();

        requestDTO = new CompetitionRequestDTO();
        requestDTO.setNom("100m");
        requestDTO.setDiscipline("Athlétisme");
        requestDTO.setStatut("A_VENIR");
    }

    @Test
    void create_ShouldReturnResponse() {
        when(competitionRepository.save(any(Competition.class))).thenReturn(competition);

        CompetitionResponseDTO response = competitionService.create(requestDTO);

        assertNotNull(response);
        assertEquals("100m", response.getNom());
    }

    @Test
    void update_ShouldUpdate_WhenExists() {
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(competitionRepository.save(any(Competition.class))).thenReturn(competition);

        CompetitionResponseDTO response = competitionService.update(1L, requestDTO);

        assertNotNull(response);
        verify(competitionRepository).save(any(Competition.class));
    }

    @Test
    void update_ShouldThrow_WhenNotFound() {
        when(competitionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> competitionService.update(1L, requestDTO));
    }

    @Test
    void findById_ShouldReturnResponse() {
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));

        CompetitionResponseDTO response = competitionService.findById(1L);

        assertEquals(1L, response.getId());
    }

    @Test
    void findById_ShouldThrow_WhenNotFound() {
        when(competitionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> competitionService.findById(1L));
    }

    @Test
    void findAll_ShouldReturnList() {
        when(competitionRepository.findAll()).thenReturn(Arrays.asList(competition));

        List<CompetitionResponseDTO> result = competitionService.findAll();

        assertEquals(1, result.size());
    }

    @Test
    void delete_ShouldCallRepo() {
        competitionService.delete(1L);
        verify(competitionRepository).deleteById(1L);
    }
}
