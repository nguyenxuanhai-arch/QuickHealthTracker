package com.vaccine.repository;

import com.vaccine.model.VaccineService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccineServiceRepository extends JpaRepository<VaccineService, Long> {
    List<VaccineService> findByNameContaining(String name);
    
    List<VaccineService> findByServiceType(VaccineService.ServiceType serviceType);
}
