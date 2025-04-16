package com.vaccine.repository;

import com.vaccine.model.Vaccine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccineRepository extends JpaRepository<Vaccine, Long> {
    List<Vaccine> findByNameContaining(String name);
    
    @Query("SELECT v FROM Vaccine v WHERE v.recommendedAgeMonthsStart <= ?1 AND (v.recommendedAgeMonthsEnd IS NULL OR v.recommendedAgeMonthsEnd >= ?1)")
    List<Vaccine> findByRecommendedAge(Integer ageInMonths);
    
    List<Vaccine> findByIsRequired(Boolean isRequired);
}
