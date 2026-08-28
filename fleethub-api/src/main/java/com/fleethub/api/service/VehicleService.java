package com.fleethub.api.service;

import com.fleethub.api.dto.CreateVehicleRequest;
import com.fleethub.api.dto.UpdateVehicleRequest;
import com.fleethub.api.dto.VehicleResponse;
import com.fleethub.api.model.VehicleStatus;

import java.util.List;

public interface VehicleService {

    VehicleResponse createVehicle(CreateVehicleRequest request);

    List<VehicleResponse> getAllVehicles(VehicleStatus status, String brand);

    VehicleResponse getVehicleById(Long id);

    VehicleResponse getVehicleByLicensePlate(String licensePlate);

    VehicleResponse updateVehicle(Long id, UpdateVehicleRequest request);

    void deleteVehicle(Long id);
}
