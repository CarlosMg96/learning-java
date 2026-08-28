package com.fleethub.api.dto;

import com.fleethub.api.model.VehicleStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateVehicleRequest {

    @NotBlank(message = "License plate is mandatory")
    @Size(min = 3, max = 20, message = "License plate must be between 3 and 20 characters")
    private String licensePlate;

    @NotBlank(message = "Brand is mandatory")
    @Size(max = 50, message = "Brand name must not exceed 50 characters")
    private String brand;

    @NotBlank(message = "Model is mandatory")
    @Size(max = 50, message = "Model name must not exceed 50 characters")
    private String model;

    @NotNull(message = "Year is mandatory")
    @Min(value = 1900, message = "Year must be greater than or equal to 1900")
    @Max(value = 2100, message = "Year must be a valid future or current year")
    private Integer year;

    @NotNull(message = "Status is mandatory")
    private VehicleStatus status;
}
