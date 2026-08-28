package com.fleethub.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleethub.api.dto.CreateVehicleRequest;
import com.fleethub.api.dto.UpdateVehicleRequest;
import com.fleethub.api.dto.VehicleResponse;
import com.fleethub.api.exception.DuplicateResourceException;
import com.fleethub.api.exception.ResourceNotFoundException;
import com.fleethub.api.model.VehicleStatus;
import com.fleethub.api.service.VehicleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración del controlador REST usando MockMvc y simulación del servicio (@MockBean).
 * Permite probar serialización JSON, validación @Valid y códigos de estado HTTP (200, 201, 400, 404, 409).
 */
@WebMvcTest(VehicleController.class)
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Simula la capa de servicio dentro del contexto web de Spring
    @MockBean
    private VehicleService vehicleService;

    @Test
    @DisplayName("POST /api/v1/vehicles - Debe retornar 201 CREATED al enviar datos válidos")
    void createVehicle_ValidPayload_Returns201() throws Exception {
        CreateVehicleRequest request = CreateVehicleRequest.builder()
                .licensePlate("XYZ-999")
                .brand("Honda")
                .model("Civic")
                .year(2022)
                .status(VehicleStatus.AVAILABLE)
                .build();

        VehicleResponse response = VehicleResponse.builder()
                .id(10L)
                .licensePlate("XYZ-999")
                .brand("Honda")
                .model("Civic")
                .year(2022)
                .status(VehicleStatus.AVAILABLE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(vehicleService.createVehicle(any(CreateVehicleRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.licensePlate", is("XYZ-999")))
                .andExpect(jsonPath("$.brand", is("Honda")));
    }

    @Test
    @DisplayName("POST /api/v1/vehicles - Debe retornar 400 BAD REQUEST cuando faltan campos requeridos")
    void createVehicle_InvalidPayload_Returns400() throws Exception {
        // Objeto con datos inválidos (placa en blanco, año nulo)
        CreateVehicleRequest invalidRequest = CreateVehicleRequest.builder()
                .licensePlate("")
                .brand("")
                .build();

        mockMvc.perform(post("/api/v1/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.validationErrors.licensePlate").exists())
                .andExpect(jsonPath("$.validationErrors.brand").exists());
    }

    @Test
    @DisplayName("POST /api/v1/vehicles - Debe retornar 409 CONFLICT cuando la placa ya existe")
    void createVehicle_DuplicatePlate_Returns409() throws Exception {
        CreateVehicleRequest request = CreateVehicleRequest.builder()
                .licensePlate("DUP-123")
                .brand("Ford")
                .model("Focus")
                .year(2020)
                .status(VehicleStatus.AVAILABLE)
                .build();

        when(vehicleService.createVehicle(any(CreateVehicleRequest.class)))
                .thenThrow(new DuplicateResourceException("Vehicle with license plate 'DUP-123' already exists"));

        mockMvc.perform(post("/api/v1/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.message", is("Vehicle with license plate 'DUP-123' already exists")));
    }

    @Test
    @DisplayName("GET /api/v1/vehicles/{id} - Debe retornar 200 OK cuando el vehículo existe")
    void getVehicleById_Exists_Returns200() throws Exception {
        VehicleResponse response = VehicleResponse.builder()
                .id(1L)
                .licensePlate("ABC-123")
                .brand("Nissan")
                .model("Versa")
                .year(2021)
                .status(VehicleStatus.AVAILABLE)
                .build();

        when(vehicleService.getVehicleById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/vehicles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.licensePlate", is("ABC-123")));
    }

    @Test
    @DisplayName("GET /api/v1/vehicles/{id} - Debe retornar 404 NOT FOUND cuando el ID no existe")
    void getVehicleById_NotFound_Returns404() throws Exception {
        when(vehicleService.getVehicleById(999L))
                .thenThrow(new ResourceNotFoundException("Vehicle with id 999 not found"));

        mockMvc.perform(get("/api/v1/vehicles/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Vehicle with id 999 not found")));
    }

    @Test
    @DisplayName("GET /api/v1/vehicles - Debe retornar 200 OK con la lista de vehículos")
    void getAllVehicles_Returns200() throws Exception {
        VehicleResponse v1 = VehicleResponse.builder().id(1L).licensePlate("AAA-111").brand("Toyota").build();
        VehicleResponse v2 = VehicleResponse.builder().id(2L).licensePlate("BBB-222").brand("Mazda").build();

        when(vehicleService.getAllVehicles(null, null)).thenReturn(List.of(v1, v2));

        mockMvc.perform(get("/api/v1/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].licensePlate", is("AAA-111")))
                .andExpect(jsonPath("$[1].licensePlate", is("BBB-222")));
    }

    @Test
    @DisplayName("PUT /api/v1/vehicles/{id} - Debe retornar 200 OK al actualizar")
    void updateVehicle_Success_Returns200() throws Exception {
        UpdateVehicleRequest updateRequest = UpdateVehicleRequest.builder()
                .brand("Toyota")
                .model("RAV4")
                .year(2023)
                .status(VehicleStatus.IN_USE)
                .build();

        VehicleResponse response = VehicleResponse.builder()
                .id(1L)
                .licensePlate("ABC-123")
                .brand("Toyota")
                .model("RAV4")
                .year(2023)
                .status(VehicleStatus.IN_USE)
                .build();

        when(vehicleService.updateVehicle(eq(1L), any(UpdateVehicleRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/vehicles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model", is("RAV4")))
                .andExpect(jsonPath("$.status", is("IN_USE")));
    }

    @Test
    @DisplayName("DELETE /api/v1/vehicles/{id} - Debe retornar 204 NO CONTENT")
    void deleteVehicle_Success_Returns204() throws Exception {
        doNothing().when(vehicleService).deleteVehicle(1L);

        mockMvc.perform(delete("/api/v1/vehicles/1"))
                .andExpect(status().isNoContent());

        verify(vehicleService, times(1)).deleteVehicle(1L);
    }
}
