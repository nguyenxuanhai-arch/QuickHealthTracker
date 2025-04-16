package com.vaccine.repository;

import com.vaccine.model.Reaction;
import com.vaccine.model.VaccineSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Optional<Reaction> findByVaccineSchedule(VaccineSchedule vaccineSchedule);
    
    Optional<Reaction> findByVaccineScheduleId(Long vaccineScheduleId);
    
    List<Reaction> findBySeverity(Reaction.Severity severity);
    
    @Query("SELECT r FROM Reaction r WHERE r.onsetTime BETWEEN ?1 AND ?2")
    List<Reaction> findByOnsetTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    @Query("SELECT r FROM Reaction r JOIN r.vaccineSchedule vs JOIN vs.vaccine v WHERE v.id = ?1")
    List<Reaction> findByVaccineId(Long vaccineId);
    
    @Query("SELECT COUNT(r) FROM Reaction r JOIN r.vaccineSchedule vs JOIN vs.vaccine v WHERE v.id = ?1 AND r.severity = ?2")
    Long countByVaccineIdAndSeverity(Long vaccineId, Reaction.Severity severity);
}
