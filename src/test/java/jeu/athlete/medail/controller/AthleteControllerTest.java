package jeu.athlete.medail.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jeu.athlete.medail.dto.AthleteRequestDTO;
import jeu.athlete.medail.dto.AthleteResponseDTO;
import jeu.athlete.medail.exception.ResourceNotFoundException;
import jeu.athlete.medail.service.AthleteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AthleteController.class)
class AthleteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AthleteService athleteService;

    @Autowired
    private ObjectMapper objectMapper;

    private AthleteResponseDTO responseDTO;
    private AthleteRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new AthleteResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNom("Marchand");
        responseDTO.setPrenom("Leon");

        requestDTO = new AthleteRequestDTO();
        requestDTO.setNom("Marchand");
        requestDTO.setPrenom("Leon");
        requestDTO.setPaysId(1L);
        requestDTO.setDiscipline("Natation");
    }

    @Test
    void getAllAthletes_ShouldReturnPage() throws Exception {
        Page<AthleteResponseDTO> page = new PageImpl<>(Arrays.asList(responseDTO));
        when(athleteService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/athletes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nom").value("Marchand"));
    }

    @Test
    void getAthleteById_ShouldReturnAthlete() throws Exception {
        when(athleteService.findById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/athletes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Marchand"));
    }

    @Test
    void getAthleteById_ShouldThrowNotFound_WhenServiceThrows() throws Exception {
        when(athleteService.findById(1L)).thenThrow(new ResourceNotFoundException("Athlète introuvable."));

        mockMvc.perform(get("/api/v1/athletes/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Athlète introuvable."));
    }

    @Test
    void createAthlete_ShouldReturnBadRequest_WhenInputInvalid() throws Exception {
        requestDTO.setNom(""); // Empty name triggers validation

        mockMvc.perform(post("/api/v1/athletes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nom").exists());
    }

    @Test
    void createAthlete_ShouldReturnCreated() throws Exception {
        when(athleteService.create(any(AthleteRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/athletes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("Marchand"));
    }

    @Test
    void updateAthlete_ShouldReturnNotFound_WhenAthleteDoesNotExist() throws Exception {
        when(athleteService.update(eq(1L), any(AthleteRequestDTO.class))).thenThrow(new ResourceNotFoundException("Oups"));

        mockMvc.perform(put("/api/v1/athletes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateAthlete_ShouldReturnUpdated() throws Exception {
        when(athleteService.update(eq(1L), any(AthleteRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/v1/athletes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Marchand"));
    }

    @Test
    void deleteAthlete_ShouldReturnInternalServerError_WhenUnexpectedException() throws Exception {
        doThrow(new RuntimeException("Oops")).when(athleteService).delete(1L);

        mockMvc.perform(delete("/api/v1/athletes/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Une erreur inattendue est survenue"));
    }

    @Test
    void deleteAthlete_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/athletes/1"))
                .andExpect(status().isNoContent());
    }
}
