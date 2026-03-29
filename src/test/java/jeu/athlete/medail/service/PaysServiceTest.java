package jeu.athlete.medail.service;

import jeu.athlete.medail.domain.Pays;
import jeu.athlete.medail.dto.PaysRequestDTO;
import jeu.athlete.medail.dto.PaysResponseDTO;
import jeu.athlete.medail.exception.ResourceNotFoundException;
import jeu.athlete.medail.repository.PaysRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaysServiceTest {

    @Mock
    private PaysRepository paysRepository;

    @InjectMocks
    private PaysService paysService;

    private Pays pays;
    private PaysRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        pays = Pays.builder()
                .id(1L)
                .code("FRA")
                .nom("France")
                .drapeau("FRA")
                .build();

        requestDTO = new PaysRequestDTO();
        requestDTO.setCode("FRA");
        requestDTO.setNom("France");
    }

    @Test
    void create_ShouldReturnResponse() {
        when(paysRepository.save(any(Pays.class))).thenReturn(pays);

        PaysResponseDTO response = paysService.create(requestDTO);

        assertNotNull(response);
        assertEquals("FRA", response.getCode());
    }

    @Test
    void update_ShouldUpdate_WhenExists() {
        when(paysRepository.findById(1L)).thenReturn(Optional.of(pays));
        when(paysRepository.save(any(Pays.class))).thenReturn(pays);

        PaysResponseDTO response = paysService.update(1L, requestDTO);

        assertNotNull(response);
        verify(paysRepository).save(any(Pays.class));
    }

    @Test
    void update_ShouldThrow_WhenNotFound() {
        when(paysRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> paysService.update(1L, requestDTO));
    }

    @Test
    void findById_ShouldReturnResponse() {
        when(paysRepository.findById(1L)).thenReturn(Optional.of(pays));
        PaysResponseDTO res = paysService.findById(1L);
        assertEquals(1L, res.getId());
    }

    @Test
    void findById_ShouldThrow_WhenNotFound() {
        when(paysRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> paysService.findById(1L));
    }

    @Test
    void findAll_ShouldReturnList() {
        when(paysRepository.findAll()).thenReturn(Arrays.asList(pays));

        List<PaysResponseDTO> result = paysService.findAll();

        assertEquals(1, result.size());
    }

    @Test
    void delete_ShouldThrow_WhenNotFound() {
        when(paysRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> paysService.delete(1L));
    }

    @Test
    void delete_ShouldCallRepo_WhenExists() {
        when(paysRepository.existsById(1L)).thenReturn(true);

        paysService.delete(1L);

        verify(paysRepository).deleteById(1L);
    }
}
