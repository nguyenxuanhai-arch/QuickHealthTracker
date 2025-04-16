package com.vaccine.repository;

import com.vaccine.model.Child;
import com.vaccine.model.Vaccine;
import com.vaccine.model.VaccineSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VaccineScheduleRepository extends JpaRepository<VaccineSchedule, Long> {
    List<VaccineSchedule> findByChild(Child child);
    
    List<VaccineSchedule> findByChildId(Long childId);
    
    List<VaccineSchedule> findByVaccine(Vaccine vaccine);
    
    List<VaccineSchedule> findByChildAndStatus(Child child, VaccineSchedule.Status status);
    
    @Query("SELECT vs FROM VaccineSchedule vs WHERE vs.scheduledDate BETWEEN ?1 AND ?2")
    List<VaccineSchedule> findByScheduledDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT vs FROM VaccineSchedule vs WHERE vs.child.id = ?1 AND vs.scheduledDate BETWEEN ?2 AND ?3")
    List<VaccineSchedule> findByChildIdAndScheduledDateBetween(Long childId, LocalDate startDate, LocalDate endDate);
}
