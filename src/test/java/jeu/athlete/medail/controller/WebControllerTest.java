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

@WebMvcTest(WebController.class)
class WebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClassementService classementService;

    @Test
    void index_ShouldReturnIndexView() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void classement_ShouldReturnClassementViewWithModel() throws Exception {
        ClassementDTO dto = new ClassementDTO();
        dto.setNomPays("France");
        when(classementService.getClassement("total")).thenReturn(Arrays.asList(dto));

        mockMvc.perform(get("/tableau-medailles"))
                .andExpect(status().isOk())
                .andExpect(view().name("classement"))
                .andExpect(model().attributeExists("classement"));
    }
}
