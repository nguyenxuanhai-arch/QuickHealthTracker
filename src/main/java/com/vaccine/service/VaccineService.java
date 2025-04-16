package com.vaccine.service;

import com.vaccine.model.Vaccine;
import com.vaccine.model.VaccineSchedule;

import java.util.List;
import java.util.Optional;

public interface VaccineService {

    Vaccine createVaccine(Vaccine vaccine);
    
    Vaccine updateVaccine(Vaccine vaccine);
    
    Optional<Vaccine> findById(Long id);
    
    List<Vaccine> findByName(String name);
    
    List<Vaccine> findByRecommendedAge(Integer ageInMonths);
    
    List<Vaccine> findByRequired(Boolean isRequired);
    
    List<Vaccine> findAll();
    
    void deleteVaccine(Long id);
    
    // Create or update a vaccine schedule
    VaccineSchedule saveVaccineSchedule(VaccineSchedule vaccineSchedule);
    
    // Find vaccine schedules by child
    List<VaccineSchedule> findVaccineSchedulesByChildId(Long childId);
    
    // Find upcoming vaccine schedules
    List<VaccineSchedule> findUpcomingVaccineSchedules(Long childId);
}
