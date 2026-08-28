package com.fleethub.api.service.impl;

import com.fleethub.api.dto.CreateVehicleRequest;
import com.fleethub.api.dto.UpdateVehicleRequest;
import com.fleethub.api.dto.VehicleResponse;
import com.fleethub.api.exception.DuplicateResourceException;
import com.fleethub.api.exception.ResourceNotFoundException;
import com.fleethub.api.model.Vehicle;
import com.fleethub.api.model.VehicleStatus;
import com.fleethub.api.repository.VehicleRepository;
import com.fleethub.api.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    @Override
    @Transactional
    public VehicleResponse createVehicle(CreateVehicleRequest request) {
        if (vehicleRepository.existsByLicensePlate(request.getLicensePlate())) {
            throw new DuplicateResourceException("Vehicle with license plate '" + request.getLicensePlate() + "' already exists");
        }

        Vehicle vehicle = Vehicle.builder()
                .licensePlate(request.getLicensePlate().toUpperCase().trim())
                .brand(request.getBrand().trim())
                .model(request.getModel().trim())
                .year(request.getYear())
                .status(request.getStatus())
                .build();

        Vehicle saved = vehicleRepository.save(vehicle);
        return VehicleResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getAllVehicles(VehicleStatus status, String brand) {
        List<Vehicle> vehicles;

        if (status != null) {
            vehicles = vehicleRepository.findByStatus(status);
        } else if (brand != null && !brand.isBlank()) {
            vehicles = vehicleRepository.findByBrandIgnoreCase(brand.trim());
        } else {
            vehicles = vehicleRepository.findAll();
        }

        return vehicles.stream()
                .map(VehicleResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponse getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle with id " + id + " not found"));
        return VehicleResponse.fromEntity(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponse getVehicleByLicensePlate(String licensePlate) {
        Vehicle vehicle = vehicleRepository.findByLicensePlate(licensePlate.toUpperCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle with license plate '" + licensePlate + "' not found"));
        return VehicleResponse.fromEntity(vehicle);
    }

    @Override
    @Transactional
    public VehicleResponse updateVehicle(Long id, UpdateVehicleRequest request) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle with id " + id + " not found"));

        vehicle.setBrand(request.getBrand().trim());
        vehicle.setModel(request.getModel().trim());
        vehicle.setYear(request.getYear());
        vehicle.setStatus(request.getStatus());

        Vehicle updated = vehicleRepository.save(vehicle);
        return VehicleResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deleteVehicle(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vehicle with id " + id + " not found");
        }
        vehicleRepository.deleteById(id);
    }
}
