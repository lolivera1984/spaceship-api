package com.spacefleet.spaceshipapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spacefleet.spaceshipapi.dto.SpaceshipDTO;
import com.spacefleet.spaceshipapi.service.SpaceshipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SpaceshipController.class)
@AutoConfigureMockMvc(addFilters = false)
class SpaceshipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockBean
    private SpaceshipService service;

    @Autowired
    private ObjectMapper objectMapper;

    private SpaceshipDTO sampleDTO;

    @BeforeEach
    void setUp() {
        sampleDTO = new SpaceshipDTO();
        sampleDTO.setId("1");
        sampleDTO.setName("X-Wing");
        sampleDTO.setModel("T-65");
    }

    @Test
    void testGetAll_shouldReturnPaginatedSpaceships() throws Exception {
        List<SpaceshipDTO> content = List.of(sampleDTO);
        Pageable pageable = PageRequest.of(0, 10);
        Page<SpaceshipDTO> page = new PageImpl<>(content, pageable, 1);

        Mockito.when(service.findAllPaginated(anyInt(), anyInt())).thenReturn(page);

        mockMvc.perform(get("/spaceships?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value("1"))
                .andExpect(jsonPath("$.content[0].name").value("X-Wing"));
    }

    @Test
    void testGetById_shouldReturnSpaceship() throws Exception {
        Mockito.when(service.findById("1")).thenReturn(sampleDTO);

        mockMvc.perform(get("/spaceships/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("X-Wing"));
    }

    @Test
    void testSearchByName_shouldReturnList() throws Exception {
        Mockito.when(service.findByName("X")).thenReturn(List.of(sampleDTO));

        mockMvc.perform(get("/spaceships/search?name=X"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("X-Wing"));
    }

    @Test
    void testCreate_shouldReturnCreatedSpaceship() throws Exception {
        Mockito.when(service.create(any())).thenReturn(sampleDTO);

        mockMvc.perform(post("/spaceships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("X-Wing"));
    }

    @Test
    void testUpdate_shouldReturnUpdatedSpaceship() throws Exception {
        Mockito.when(service.update(eq("1"), any())).thenReturn(sampleDTO);

        mockMvc.perform(put("/spaceships/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("X-Wing"));
    }

    @Test
    void testDelete_shouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(service).delete("1");

        mockMvc.perform(delete("/spaceships/1"))
                .andExpect(status().isNoContent());
    }
}
