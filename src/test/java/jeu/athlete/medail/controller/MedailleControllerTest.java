package jeu.athlete.medail.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jeu.athlete.medail.domain.TypeMedaille;
import jeu.athlete.medail.dto.MedailleRequestDTO;
import jeu.athlete.medail.dto.MedailleResponseDTO;
import jeu.athlete.medail.service.MedailleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MedailleController.class)
class MedailleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedailleService medailleService;

    @Autowired
    private ObjectMapper objectMapper;

    private MedailleResponseDTO responseDTO;
    private MedailleRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new MedailleResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setType(TypeMedaille.OR);
        responseDTO.setNomAthlete("Leon Marchand");

        requestDTO = new MedailleRequestDTO();
        requestDTO.setAthleteId(1L);
        requestDTO.setCompetitionId(1L);
        requestDTO.setType(TypeMedaille.OR);
        requestDTO.setDateObtention(LocalDate.now());
    }

    @Test
    void getAllMedailles_ShouldReturnList() throws Exception {
        when(medailleService.findAll()).thenReturn(Arrays.asList(responseDTO));

        mockMvc.perform(get("/api/v1/medailles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nomAthlete").value("Leon Marchand"));
    }

    @Test
    void createMedaille_ShouldReturnCreated() throws Exception {
        when(medailleService.creer(any(MedailleRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/medailles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("OR"));
    }

    @Test
    void getMedailleById_ShouldReturnMedaille() throws Exception {
        when(medailleService.findById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/medailles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomAthlete").value("Leon Marchand"));
    }

    @Test
    void createMedaille_ShouldReturnNotFound_WhenServiceThrows() throws Exception {
        when(medailleService.creer(any(MedailleRequestDTO.class))).thenThrow(new jeu.athlete.medail.exception.ResourceNotFoundException("Oups"));

        mockMvc.perform(post("/api/v1/medailles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMedaillesByAthlete_ShouldReturnList() throws Exception {
        when(medailleService.findByAthleteId(1L)).thenReturn(Arrays.asList(responseDTO));

        mockMvc.perform(get("/api/v1/medailles/athlete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nomAthlete").value("Leon Marchand"));
    }

    @Test
    void getMedaillesByCompetition_ShouldReturnList() throws Exception {
        when(medailleService.findByCompetitionId(1L)).thenReturn(Arrays.asList(responseDTO));

        mockMvc.perform(get("/api/v1/medailles/competition/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nomAthlete").value("Leon Marchand"));
    }
}
