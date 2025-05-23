package stud.ntnu.krisefikser.map.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import stud.ntnu.krisefikser.config.TestSecurityConfig;
import stud.ntnu.krisefikser.map.entity.MapPointType;
import stud.ntnu.krisefikser.map.service.MapPointTypeService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MapPointTypeController.class)
@Import(TestSecurityConfig.class)
class MapPointTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MapPointTypeService mapPointTypeService;

    @Autowired
    private ObjectMapper objectMapper;

    private MapPointType testMapPointType;
    private List<MapPointType> testMapPointTypes;

    @BeforeEach
    void setUp() {
        testMapPointType = MapPointType.builder()
                .id(1L)
                .title("Test Point Type")
                .iconUrl("http://example.com/icon.png")
                .description("Test Description")
                .openingTime("9:00-17:00")
                .build();

        testMapPointTypes = Arrays.asList(testMapPointType);
    }

    @Test
    void getAllMapPointTypes_ShouldReturnList() throws Exception {
        when(mapPointTypeService.getAllMapPointTypes()).thenReturn(testMapPointTypes);

        mockMvc.perform(get("/api/map-point-types"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(testMapPointType.getId()))
                .andExpect(jsonPath("$[0].title").value(testMapPointType.getTitle()));
    }

    @Test
    void getMapPointTypeById_ShouldReturnMapPointType() throws Exception {
        when(mapPointTypeService.getMapPointTypeById(1L)).thenReturn(testMapPointType);

        mockMvc.perform(get("/api/map-point-types/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testMapPointType.getId()))
                .andExpect(jsonPath("$.title").value(testMapPointType.getTitle()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createMapPointType_WithAdminRole_ShouldCreateAndReturnMapPointType() throws Exception {
        when(mapPointTypeService.createMapPointType(any(MapPointType.class))).thenReturn(testMapPointType);

        mockMvc.perform(post("/api/map-point-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMapPointType)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testMapPointType.getId()))
                .andExpect(jsonPath("$.title").value(testMapPointType.getTitle()));
    }

    @Test
    void createMapPointType_WithoutAdminRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/map-point-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMapPointType)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateMapPointType_WithAdminRole_ShouldUpdateAndReturnMapPointType() throws Exception {
        when(mapPointTypeService.updateMapPointType(eq(1L), any(MapPointType.class))).thenReturn(testMapPointType);

        mockMvc.perform(put("/api/map-point-types/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMapPointType)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testMapPointType.getId()))
                .andExpect(jsonPath("$.title").value(testMapPointType.getTitle()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteMapPointType_WithAdminRole_ShouldReturnNoContent() throws Exception {
        doNothing().when(mapPointTypeService).deleteMapPointType(1L);

        mockMvc.perform(delete("/api/map-point-types/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteMapPointType_WithoutAdminRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/api/map-point-types/1"))
                .andExpect(status().isForbidden());
    }
}