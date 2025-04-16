package com.vaccine.service.impl;

import com.vaccine.model.Child;
import com.vaccine.model.User;
import com.vaccine.model.Vaccine;
import com.vaccine.model.VaccineSchedule;
import com.vaccine.repository.ChildRepository;
import com.vaccine.repository.VaccineRepository;
import com.vaccine.repository.VaccineScheduleRepository;
import com.vaccine.service.ChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Service
public class ChildServiceImpl implements ChildService {

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private VaccineRepository vaccineRepository;

    @Autowired
    private VaccineScheduleRepository vaccineScheduleRepository;

    @Override
    @Transactional
    public Child createChild(Child child, User parent) {
        child.setParent(parent);
        Child savedChild = childRepository.save(child);
        
        // Schedule vaccines for the child based on age
        scheduleVaccinesForChild(savedChild);
        
        return savedChild;
    }

    @Override
    @Transactional
    public Child updateChild(Child child) {
        if (!childRepository.existsById(child.getId())) {
            throw new RuntimeException("Child not found!");
        }
        return childRepository.save(child);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Child> findById(Long id) {
        return childRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Child> findByParent(User parent) {
        return childRepository.findByParent(parent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Child> findByParentId(Long parentId) {
        return childRepository.findByParentId(parentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Child> findAll() {
        return childRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteChild(Long id) {
        childRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void scheduleVaccinesForChild(Child child) {
        // Calculate child's age in months
        LocalDate now = LocalDate.now();
        Period period = Period.between(child.getDateOfBirth(), now);
        int ageInMonths = period.getYears() * 12 + period.getMonths();
        
        // Find appropriate vaccines for the child's age
        List<Vaccine> recommendedVaccines = vaccineRepository.findByRecommendedAge(ageInMonths);
        
        // Create vaccine schedules for each recommended vaccine
        for (Vaccine vaccine : recommendedVaccines) {
            // Check if the child already has this vaccine scheduled or administered
            boolean alreadyScheduled = vaccineScheduleRepository.findByChildId(child.getId()).stream()
                    .anyMatch(vs -> vs.getVaccine().getId().equals(vaccine.getId()));
            
            // If not already scheduled, create a new schedule
            if (!alreadyScheduled) {
                VaccineSchedule vaccineSchedule = new VaccineSchedule();
                vaccineSchedule.setChild(child);
                vaccineSchedule.setVaccine(vaccine);
                vaccineSchedule.setScheduledDate(now.plusDays(7)); // Schedule for next week by default
                vaccineSchedule.setStatus(VaccineSchedule.Status.SCHEDULED);
                vaccineScheduleRepository.save(vaccineSchedule);
            }
        }
    }
}
