package com.fleethub.api.repository;

import com.fleethub.api.model.Vehicle;
import com.fleethub.api.model.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByLicensePlate(String licensePlate);

    boolean existsByLicensePlate(String licensePlate);

    List<Vehicle> findByStatus(VehicleStatus status);

    List<Vehicle> findByBrandIgnoreCase(String brand);
}
