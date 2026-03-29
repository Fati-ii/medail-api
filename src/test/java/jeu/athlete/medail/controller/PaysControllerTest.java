package jeu.athlete.medail.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jeu.athlete.medail.dto.PaysRequestDTO;
import jeu.athlete.medail.dto.PaysResponseDTO;
import jeu.athlete.medail.service.PaysService;
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

@WebMvcTest(PaysController.class)
class PaysControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaysService paysService;

    @Autowired
    private ObjectMapper objectMapper;

    private PaysResponseDTO responseDTO;
    private PaysRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new PaysResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setCode("FRA");
        responseDTO.setNom("France");

        requestDTO = new PaysRequestDTO();
        requestDTO.setCode("FRA");
        requestDTO.setNom("France");
    }

    @Test
    void getAllPays_ShouldReturnList() throws Exception {
        when(paysService.findAll()).thenReturn(Arrays.asList(responseDTO));

        mockMvc.perform(get("/api/v1/pays"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("France"));
    }

    @Test
    void getPaysById_ShouldReturnPays() throws Exception {
        when(paysService.findById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/pays/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("France"));
    }

    @Test
    void createPays_ShouldReturnCreated() throws Exception {
        when(paysService.create(any(PaysRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/pays")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("France"));
    }

    @Test
    void updatePays_ShouldReturnUpdated() throws Exception {
        when(paysService.update(eq(1L), any(PaysRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/v1/pays/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("France"));
    }

    @Test
    void deletePays_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/pays/1"))
                .andExpect(status().isNoContent());
    }
}
