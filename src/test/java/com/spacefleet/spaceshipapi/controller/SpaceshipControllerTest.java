package com.spacefleet.spaceshipapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spacefleet.spaceshipapi.dto.PaginatedResponseDTO;
import com.spacefleet.spaceshipapi.dto.SpaceshipDTO;
import com.spacefleet.spaceshipapi.service.SpaceshipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasKey;
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
        sampleDTO = new SpaceshipDTO("1","X-Wing","T-65", "SpaceX");
    }

    @Test
    void testGetAll_shouldReturnPaginatedSpaceships() throws Exception {
        List<SpaceshipDTO> content = List.of(sampleDTO);
        PaginatedResponseDTO<SpaceshipDTO> responseDTO = new PaginatedResponseDTO<>(content, 0, 10, 1L,1);

        Mockito.when(service.findAllPaginated(anyInt(), anyInt())).thenReturn(responseDTO);

        mockMvc.perform(get("/spaceships?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value("1"))
                .andExpect(jsonPath("$.content[0].name").value("X-Wing"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
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
        List<SpaceshipDTO> content = List.of(sampleDTO);
        PaginatedResponseDTO<SpaceshipDTO> responseDTO = new PaginatedResponseDTO<>(content, 0, 10, 1L,1);

        Mockito.when(service.findByNamePaginated("X",1, 10)).thenReturn(responseDTO);

        mockMvc.perform(get("/spaceships/search?name=X&page=1&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("X-Wing"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));

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

    @Test
    void testGetById_shouldFail_whenIdIsBlank() throws Exception {
        mockMvc.perform(get("/spaceships/ "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasKey("getById.id")));
    }

    @Test
    void testGetById_shouldFail_whenIdTooLong() throws Exception {
        String longId = "a".repeat(51);
        mockMvc.perform(get("/spaceships/" + longId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasKey("getById.id")));
    }

    @Test
    void testSearchByName_shouldFail_whenNameTooLong() throws Exception {
        String longName = "a".repeat(101);
        mockMvc.perform(get("/spaceships/search")
                        .param("name", longName)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasKey("searchByName.name")));
    }

    @Test
    void testDelete_shouldFail_whenIdIsBlank() throws Exception {
        mockMvc.perform(delete("/spaceships/ "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasKey("delete.id")));
    }

    @Test
    void testCreate_shouldFail_whenFieldsAreBlank() throws Exception {
        String requestBody = """
        {
            "id": "123",
            "name": "",
            "model": "",
            "manufacturer": ""
        }
        """;

        mockMvc.perform(post("/spaceships")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").value("Spaceship Name is required"));
    }

    @Test
    void testUpdate_shouldFail_whenFieldsAreBlank() throws Exception {
        String requestBody = """
        {
            "id": "123",
            "name": "",
            "model": "",
            "manufacturer": ""
        }
        """;

        mockMvc.perform(put("/spaceships/123")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").value("Spaceship Name is required"));
    }
}
