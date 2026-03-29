package jeu.athlete.medail.service;

import jeu.athlete.medail.domain.Athlete;
import jeu.athlete.medail.domain.Pays;
import jeu.athlete.medail.dto.AthleteRequestDTO;
import jeu.athlete.medail.dto.AthleteResponseDTO;
import jeu.athlete.medail.exception.ResourceNotFoundException;
import jeu.athlete.medail.repository.AthleteRepository;
import jeu.athlete.medail.repository.PaysRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AthleteServiceTest {

    @Mock
    private AthleteRepository athleteRepository;

    @Mock
    private PaysRepository paysRepository;

    @Mock
    private PaysService paysService;

    @InjectMocks
    private AthleteService athleteService;

    private Pays pays;
    private Athlete athlete;
    private AthleteRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        pays = new Pays();
        pays.setId(1L);
        pays.setNom("France");

        athlete = Athlete.builder()
                .id(1L)
                .nom("Marchand")
                .prenom("Leon")
                .discipline("Natation")
                .pays(pays)
                .build();

        requestDTO = new AthleteRequestDTO();
        requestDTO.setNom("Marchand");
        requestDTO.setPrenom("Leon");
        requestDTO.setPaysId(1L);
        requestDTO.setDiscipline("Natation");
        requestDTO.setDateNaissance(LocalDate.of(2002, 5, 17));
    }

    @Test
    void create_ShouldReturnAthleteResponseDTO_WhenPaysExists() {
        when(paysRepository.findById(1L)).thenReturn(Optional.of(pays));
        when(athleteRepository.save(any(Athlete.class))).thenReturn(athlete);

        AthleteResponseDTO response = athleteService.create(requestDTO);

        assertNotNull(response);
        assertEquals("Marchand", response.getNom());
        verify(athleteRepository).save(any(Athlete.class));
    }

    @Test
    void create_ShouldThrowException_WhenPaysDoesNotExist() {
        when(paysRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> athleteService.create(requestDTO));
    }

    @Test
    void update_ShouldUpdateAthlete_WhenAthleteAndPaysExist() {
        when(athleteRepository.findById(1L)).thenReturn(Optional.of(athlete));
        when(paysRepository.findById(1L)).thenReturn(Optional.of(pays));
        when(athleteRepository.save(any(Athlete.class))).thenReturn(athlete);

        AthleteResponseDTO response = athleteService.update(1L, requestDTO);

        assertNotNull(response);
        verify(athleteRepository).save(athlete);
    }

    @Test
    void update_ShouldThrowException_WhenAthleteDoesNotExist() {
        when(athleteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> athleteService.update(1L, requestDTO));
    }

    @Test
    void update_ShouldThrowException_WhenPaysDoesNotExist() {
        when(athleteRepository.findById(1L)).thenReturn(Optional.of(athlete));
        when(paysRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> athleteService.update(1L, requestDTO));
    }

    @Test
    void findById_ShouldReturnAthlete_WhenExists() {
        when(athleteRepository.findById(1L)).thenReturn(Optional.of(athlete));

        AthleteResponseDTO response = athleteService.findById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void findById_ShouldThrow_WhenNotFound() {
        when(athleteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> athleteService.findById(1L));
    }

    @Test
    void findAll_ShouldReturnPageOfAthletes() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Athlete> page = new PageImpl<>(Arrays.asList(athlete));
        when(athleteRepository.findAll(pageable)).thenReturn(page);

        Page<AthleteResponseDTO> result = athleteService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void findByPaysId_ShouldReturnList() {
        when(athleteRepository.findByPaysId(1L)).thenReturn(Arrays.asList(athlete));

        List<AthleteResponseDTO> result = athleteService.findByPaysId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void delete_ShouldCallRepository() {
        athleteService.delete(1L);
        verify(athleteRepository).deleteById(1L);
    }
}
