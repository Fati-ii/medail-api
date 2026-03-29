package jeu.athlete.medail.service;

import jeu.athlete.medail.domain.Athlete;
import jeu.athlete.medail.domain.Competition;
import jeu.athlete.medail.domain.Medaille;
import jeu.athlete.medail.domain.TypeMedaille;
import jeu.athlete.medail.domain.Pays;
import jeu.athlete.medail.dto.MedailleRequestDTO;
import jeu.athlete.medail.dto.MedailleResponseDTO;
import jeu.athlete.medail.exception.ResourceNotFoundException;
import jeu.athlete.medail.repository.AthleteRepository;
import jeu.athlete.medail.repository.CompetitionRepository;
import jeu.athlete.medail.repository.MedailleRepository;
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
class MedailleServiceTest {

    @Mock
    private MedailleRepository medailleRepository;

    @Mock
    private AthleteRepository athleteRepository;

    @Mock
    private CompetitionRepository competitionRepository;

    @InjectMocks
    private MedailleService medailleService;

    private Athlete athlete;
    private Competition competition;
    private Pays pays;
    private MedailleRequestDTO requestDTO;
    private Medaille medaille;

    @BeforeEach
    void setUp() {
        pays = new Pays();
        pays.setId(1L);
        pays.setNom("France");

        athlete = new Athlete();
        athlete.setId(1L);
        athlete.setNom("Marchand");
        athlete.setPrenom("Leon");
        athlete.setPays(pays);

        competition = new Competition();
        competition.setId(1L);
        competition.setNom("400m 4 nages");

        requestDTO = new MedailleRequestDTO();
        requestDTO.setAthleteId(1L);
        requestDTO.setCompetitionId(1L);
        requestDTO.setType(TypeMedaille.OR);
        requestDTO.setDateObtention(LocalDate.now());

        medaille = new Medaille();
        medaille.setId(1L);
        medaille.setType(TypeMedaille.OR);
        medaille.setAthlete(athlete);
        medaille.setCompetition(competition);
        medaille.setPays(pays);
    }

    @Test
    void creer_ShouldReturnMedailleResponseDTO_WhenAthleteAndCompetitionExist() {
        when(athleteRepository.findById(1L)).thenReturn(Optional.of(athlete));
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(medailleRepository.save(any(Medaille.class))).thenReturn(medaille);

        MedailleResponseDTO response = medailleService.creer(requestDTO);

        assertNotNull(response);
        assertEquals(TypeMedaille.OR, response.getType());
        assertEquals(1L, response.getAthleteId());
        verify(medailleRepository).save(any(Medaille.class));
    }

    @Test
    void creer_ShouldThrowException_WhenAthleteNotFound() {
        when(athleteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> medailleService.creer(requestDTO));
    }

    @Test
    void findById_ShouldReturnMedaille_WhenExists() {
        when(medailleRepository.findById(1L)).thenReturn(Optional.of(medaille));

        MedailleResponseDTO response = medailleService.findById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void findById_ShouldThrow_WhenNotFound() {
        when(medailleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> medailleService.findById(1L));
    }

    @Test
    void mapToDTO_ShouldHandleAllNullRelations() {
        Medaille m = new Medaille();
        m.setId(1L);
        m.setType(TypeMedaille.OR);
        m.setAthlete(null);
        m.setPays(null);
        m.setCompetition(null);

        when(medailleRepository.findByAthleteId(1L)).thenReturn(Arrays.asList(m));
        
        List<MedailleResponseDTO> results = medailleService.findByAthleteId(1L);
        assertNotNull(results.get(0));
        assertNull(results.get(0).getNomAthlete());
        assertNull(results.get(0).getNomPays());
        assertNull(results.get(0).getNomCompetition());
    }

    @Test
    void creer_ShouldHandleNullDate() {
        requestDTO.setDateObtention(null);
        when(athleteRepository.findById(1L)).thenReturn(Optional.of(athlete));
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(medailleRepository.save(any(Medaille.class))).thenReturn(medaille);

        MedailleResponseDTO response = medailleService.creer(requestDTO);
        assertNotNull(response);
    }
    @Test
    void mapToDTO_ShouldHandlePartialNullRelations() {
        Medaille m1 = new Medaille();
        m1.setId(1L);
        m1.setAthlete(athlete);
        m1.setPays(null);
        m1.setCompetition(null);

        Medaille m2 = new Medaille();
        m2.setId(2L);
        m2.setAthlete(null);
        m2.setPays(pays);
        m2.setCompetition(null);

        Medaille m3 = new Medaille();
        m3.setId(3L);
        m3.setAthlete(null);
        m3.setPays(null);
        m3.setCompetition(competition);

        when(medailleRepository.findAll()).thenReturn(Arrays.asList(m1, m2, m3));
        
        List<MedailleResponseDTO> results = medailleService.findAll();
        assertEquals(3, results.size());
    }

    @Test
    void findAll_ShouldReturnList() {
        when(medailleRepository.findAll()).thenReturn(Arrays.asList(medaille));

        List<MedailleResponseDTO> result = medailleService.findAll();

        assertEquals(1, result.size());
    }

    @Test
    void findByAthleteId_ShouldReturnList() {
        when(medailleRepository.findByAthleteId(1L)).thenReturn(Arrays.asList(medaille));

        List<MedailleResponseDTO> result = medailleService.findByAthleteId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void findByCompetitionId_ShouldReturnList() {
        when(medailleRepository.findByCompetitionId(1L)).thenReturn(Arrays.asList(medaille));

        List<MedailleResponseDTO> result = medailleService.findByCompetitionId(1L);

        assertEquals(1, result.size());
    }
}
