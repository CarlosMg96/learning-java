package com.fleethub.api.controller;

import com.fleethub.api.dto.CreateVehicleRequest;
import com.fleethub.api.dto.UpdateVehicleRequest;
import com.fleethub.api.dto.VehicleResponse;
import com.fleethub.api.model.VehicleStatus;
import com.fleethub.api.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<VehicleResponse> createVehicle(@Valid @RequestBody CreateVehicleRequest request) {
        VehicleResponse response = vehicleService.createVehicle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<VehicleResponse>> getAllVehicles(
            @RequestParam(required = false) VehicleStatus status,
            @RequestParam(required = false) String brand) {
        List<VehicleResponse> vehicles = vehicleService.getAllVehicles(status, brand);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable Long id) {
        VehicleResponse response = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/plate/{licensePlate}")
    public ResponseEntity<VehicleResponse> getVehicleByLicensePlate(@PathVariable String licensePlate) {
        VehicleResponse response = vehicleService.getVehicleByLicensePlate(licensePlate);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponse> updateVehicle(
            @PathVariable Long id,
            @Valid @RequestBody UpdateVehicleRequest request) {
        VehicleResponse response = vehicleService.updateVehicle(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}
