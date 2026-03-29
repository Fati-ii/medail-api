package jeu.athlete.medail.service;

import jeu.athlete.medail.domain.Medaille;
import jeu.athlete.medail.domain.Pays;
import jeu.athlete.medail.domain.TypeMedaille;
import jeu.athlete.medail.dto.ClassementDTO;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClassementServiceTest {

    @Mock
    private PaysRepository paysRepository;

    @InjectMocks
    private ClassementService classementService;

    private Pays paysFrance;
    private Pays paysUSA;

    @BeforeEach
    void setUp() {
        paysFrance = new Pays();
        paysFrance.setId(1L);
        paysFrance.setNom("France");
        paysFrance.setCode("FRA");
        
        Medaille p1Or = new Medaille(); p1Or.setType(TypeMedaille.OR);
        Medaille p1Bronze = new Medaille(); p1Bronze.setType(TypeMedaille.BRONZE);
        paysFrance.setMedailles(Arrays.asList(p1Or, p1Bronze)); // 3 + 1 = 4 points

        paysUSA = new Pays();
        paysUSA.setId(2L);
        paysUSA.setNom("USA");
        paysUSA.setCode("USA");
        
        Medaille p2Argent1 = new Medaille(); p2Argent1.setType(TypeMedaille.ARGENT);
        Medaille p2Argent2 = new Medaille(); p2Argent2.setType(TypeMedaille.ARGENT);
        paysUSA.setMedailles(Arrays.asList(p2Argent1, p2Argent2)); // 2 + 2 = 4 points
    }

    @Test
    void getClassement_ShouldSortByDefault_WhenTriIsNull() {
        when(paysRepository.findAll()).thenReturn(Arrays.asList(paysFrance));
        List<ClassementDTO> res = classementService.getClassement(null);
        assertNotNull(res);
    }

    @Test
    void getClassement_ShouldSortByTotalFull() {
        // Default case
        when(paysRepository.findAll()).thenReturn(Arrays.asList(paysFrance));
        List<ClassementDTO> res = classementService.getClassement("total");
        assertNotNull(res);
    }

    @Test
    void getClassement_ShouldSortByOr() {
        Pays p2 = new Pays();
        p2.setId(2L);
        p2.setNom("USA");
        Medaille m = new Medaille();
        m.setType(TypeMedaille.OR);
        p2.setMedailles(Arrays.asList(m, m)); // 2 Gold for USA
        
        when(paysRepository.findAll()).thenReturn(Arrays.asList(paysFrance, p2));

        List<ClassementDTO> res = classementService.getClassement("or");
        assertEquals("USA", res.get(0).getNomPays());
    }

    @Test
    void getClassement_ShouldSortByPoints() {
        Pays p2 = new Pays();
        p2.setId(2L);
        p2.setNom("USA");
        Medaille m = new Medaille();
        m.setType(TypeMedaille.ARGENT);
        p2.setMedailles(Arrays.asList(m, m, m)); // 6 points for USA (3 Silver)
        
        Medaille m2 = new Medaille();
        m2.setType(TypeMedaille.OR);
        paysFrance.setMedailles(Arrays.asList(m2)); // 3 points for France (1 Gold)
        
        when(paysRepository.findAll()).thenReturn(Arrays.asList(paysFrance, p2));

        List<ClassementDTO> res = classementService.getClassement("points");
        assertEquals("USA", res.get(0).getNomPays());
    }

    @Test
    void getClassement_ShouldHandleEmptyMedailles() {
        paysFrance.setMedailles(null);
        when(paysRepository.findAll()).thenReturn(Arrays.asList(paysFrance));
        
        List<ClassementDTO> res = classementService.getClassement("total");
        assertEquals(0, res.get(0).getTotal());
    }

    @Test
    void getStatsPays_ShouldReturnStats() {
        when(paysRepository.findById(1L)).thenReturn(Optional.of(paysFrance));
        
        ClassementDTO res = classementService.getStatsPays(1L);
        assertEquals("France", res.getNomPays());
    }

    @Test
    void getStatsPays_ShouldThrow_WhenNotFound() {
        when(paysRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> classementService.getStatsPays(1L));
    }

    @Test
    void getClassement_ShouldReturnList() {
        when(paysRepository.findAll()).thenReturn(Arrays.asList(paysFrance, paysUSA));
        
        List<ClassementDTO> result = classementService.getClassement("or");
        
        assertEquals(2, result.size());
        assertEquals("France", result.get(0).getNomPays()); // France a 1 Or
        assertEquals(1, result.get(0).getOrCount());
        assertEquals(0, result.get(1).getOrCount());
    }

    @Test
    void testGetStatsPays() {
        when(paysRepository.findById(1L)).thenReturn(java.util.Optional.of(paysFrance));
        
        ClassementDTO stats = classementService.getStatsPays(1L);
        
        assertEquals(4, stats.getPoints());
        assertEquals(2, stats.getTotal());
        assertEquals(1, stats.getOrCount());
    }
}
