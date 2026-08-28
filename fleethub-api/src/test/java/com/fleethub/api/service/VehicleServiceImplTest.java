package com.fleethub.api.service;

import com.fleethub.api.dto.CreateVehicleRequest;
import com.fleethub.api.dto.UpdateVehicleRequest;
import com.fleethub.api.dto.VehicleResponse;
import com.fleethub.api.exception.DuplicateResourceException;
import com.fleethub.api.exception.ResourceNotFoundException;
import com.fleethub.api.model.Vehicle;
import com.fleethub.api.model.VehicleStatus;
import com.fleethub.api.repository.VehicleRepository;
import com.fleethub.api.service.impl.VehicleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de la lógica de negocio (VehicleService) usando Mockito (Mocks).
 * Se simula el repositorio de base de datos para no requerir conexión real a PostgreSQL.
 */
@ExtendWith(MockitoExtension.class)
class VehicleServiceImplTest {

    // 1. @Mock: Crea un objeto simulado (Mock) del repositorio
    @Mock
    private VehicleRepository vehicleRepository;

    // 2. @InjectMocks: Inyecta los mocks (@Mock) automáticamente en el servicio que vamos a probar
    @InjectMocks
    private VehicleServiceImpl vehicleService;

    private Vehicle sampleVehicle;
    private CreateVehicleRequest createRequest;

    @BeforeEach
    void setUp() {
        sampleVehicle = Vehicle.builder()
                .id(1L)
                .licensePlate("ABC-123")
                .brand("Toyota")
                .model("Corolla")
                .year(2023)
                .status(VehicleStatus.AVAILABLE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createRequest = CreateVehicleRequest.builder()
                .licensePlate("ABC-123")
                .brand("Toyota")
                .model("Corolla")
                .year(2023)
                .status(VehicleStatus.AVAILABLE)
                .build();
    }

    @Test
    @DisplayName("Debe crear un vehículo exitosamente cuando la placa no existe")
    void createVehicle_Success() {
        // GIVEN: Simulamos que la placa NO existe previamente y que el repositorio guarda con éxito
        when(vehicleRepository.existsByLicensePlate("ABC-123")).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(sampleVehicle);

        // WHEN: Ejecutamos el método del servicio
        VehicleResponse response = vehicleService.createVehicle(createRequest);

        // THEN: Verificamos el resultado
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getLicensePlate()).isEqualTo("ABC-123");
        assertThat(response.getBrand()).isEqualTo("Toyota");

        // Verificamos que se llamó al repositorio para guardar
        verify(vehicleRepository, times(1)).existsByLicensePlate("ABC-123");
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Debe lanzar DuplicateResourceException si la placa ya existe")
    void createVehicle_DuplicatePlate_ThrowsException() {
        // GIVEN: Simulamos que la placa YA existe
        when(vehicleRepository.existsByLicensePlate(anyString())).thenReturn(true);

        // WHEN & THEN: Verificamos que se lance la excepción esperada
        assertThatThrownBy(() -> vehicleService.createVehicle(createRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already exists");

        // Verificamos que NUNCA se intentó guardar en la base de datos
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Debe retornar un vehículo por su ID cuando existe")
    void getVehicleById_Success() {
        // GIVEN
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(sampleVehicle));

        // WHEN
        VehicleResponse response = vehicleService.getVehicleById(1L);

        // THEN
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        verify(vehicleRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando el ID no existe")
    void getVehicleById_NotFound_ThrowsException() {
        // GIVEN
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThatThrownBy(() -> vehicleService.getVehicleById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vehicle with id 99 not found");

        verify(vehicleRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Debe listar todos los vehículos")
    void getAllVehicles_NoFilters_ReturnsAll() {
        // GIVEN
        when(vehicleRepository.findAll()).thenReturn(List.of(sampleVehicle));

        // WHEN
        List<VehicleResponse> responses = vehicleService.getAllVehicles(null, null);

        // THEN
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getLicensePlate()).isEqualTo("ABC-123");
        verify(vehicleRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe actualizar un vehículo existente correctamente")
    void updateVehicle_Success() {
        // GIVEN
        UpdateVehicleRequest updateRequest = UpdateVehicleRequest.builder()
                .brand("Toyota")
                .model("Corolla Cross")
                .year(2024)
                .status(VehicleStatus.IN_USE)
                .build();

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(sampleVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(sampleVehicle);

        // WHEN
        VehicleResponse response = vehicleService.updateVehicle(1L, updateRequest);

        // THEN
        assertThat(response).isNotNull();
        verify(vehicleRepository, times(1)).findById(1L);
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Debe eliminar un vehículo cuando el ID existe")
    void deleteVehicle_Success() {
        // GIVEN
        when(vehicleRepository.existsById(1L)).thenReturn(true);
        doNothing().when(vehicleRepository).deleteById(1L);

        // WHEN
        vehicleService.deleteVehicle(1L);

        // THEN
        verify(vehicleRepository, times(1)).existsById(1L);
        verify(vehicleRepository, times(1)).deleteById(1L);
    }
}
