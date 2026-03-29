package jeu.athlete.medail.controller;

import jeu.athlete.medail.dto.ClassementDTO;
import jeu.athlete.medail.service.ClassementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClassementController.class)
class ClassementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClassementService classementService;

    @Test
    void getClassement_ShouldReturnList() throws Exception {
        ClassementDTO dto = new ClassementDTO();
        dto.setNomPays("France");
        when(classementService.getClassement("or")).thenReturn(Arrays.asList(dto));

        mockMvc.perform(get("/api/v1/classement?tri=or"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nomPays").value("France"));
    }

    @Test
    void getStatsPays_ShouldReturnStats() throws Exception {
        ClassementDTO dto = new ClassementDTO();
        dto.setNomPays("France");
        when(classementService.getStatsPays(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/classement/pays/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomPays").value("France"));
    }
}
