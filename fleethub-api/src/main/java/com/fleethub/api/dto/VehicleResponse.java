package com.fleethub.api.dto;

import com.fleethub.api.model.Vehicle;
import com.fleethub.api.model.VehicleStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponse {

    private Long id;
    private String licensePlate;
    private String brand;
    private String model;
    private Integer year;
    private VehicleStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static VehicleResponse fromEntity(Vehicle vehicle) {
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .licensePlate(vehicle.getLicensePlate())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .year(vehicle.getYear())
                .status(vehicle.getStatus())
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .build();
    }
}
