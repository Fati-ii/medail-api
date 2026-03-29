package jeu.athlete.medail.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jeu.athlete.medail.dto.CompetitionRequestDTO;
import jeu.athlete.medail.dto.CompetitionResponseDTO;
import jeu.athlete.medail.service.CompetitionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CompetitionController.class)
class CompetitionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompetitionService competitionService;

    @Autowired
    private ObjectMapper objectMapper;

    private CompetitionResponseDTO responseDTO;
    private CompetitionRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new CompetitionResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNom("100m");

        requestDTO = new CompetitionRequestDTO();
        requestDTO.setNom("100m");
    }

    @Test
    void getAllCompetitions_ShouldReturnList() throws Exception {
        when(competitionService.findAll()).thenReturn(Arrays.asList(responseDTO));

        mockMvc.perform(get("/api/v1/competitions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("100m"));
    }

    @Test
    void getCompetitionById_ShouldReturnCompetition() throws Exception {
        when(competitionService.findById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/competitions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("100m"));
    }

    @Test
    void getCompetitionById_ShouldReturnNotFound() throws Exception {
        when(competitionService.findById(1L)).thenThrow(new jeu.athlete.medail.exception.ResourceNotFoundException("Oups"));

        mockMvc.perform(get("/api/v1/competitions/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createCompetition_ShouldReturnCreated() throws Exception {
        when(competitionService.create(any(CompetitionRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/competitions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("100m"));
    }

    @Test
    void updateCompetition_ShouldReturnUpdated() throws Exception {
        when(competitionService.update(eq(1L), any(CompetitionRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/v1/competitions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("100m"));
    }

    @Test
    void deleteCompetition_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/competitions/1"))
                .andExpect(status().isNoContent());
    }
}
