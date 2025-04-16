package com.vaccine.service.impl;

import com.vaccine.model.Vaccine;
import com.vaccine.model.VaccineSchedule;
import com.vaccine.repository.VaccineRepository;
import com.vaccine.repository.VaccineScheduleRepository;
import com.vaccine.service.VaccineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VaccineServiceImpl implements VaccineService {

    @Autowired
    private VaccineRepository vaccineRepository;

    @Autowired
    private VaccineScheduleRepository vaccineScheduleRepository;

    @Override
    @Transactional
    public Vaccine createVaccine(Vaccine vaccine) {
        return vaccineRepository.save(vaccine);
    }

    @Override
    @Transactional
    public Vaccine updateVaccine(Vaccine vaccine) {
        if (!vaccineRepository.existsById(vaccine.getId())) {
            throw new RuntimeException("Vaccine not found!");
        }
        return vaccineRepository.save(vaccine);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Vaccine> findById(Long id) {
        return vaccineRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vaccine> findByName(String name) {
        return vaccineRepository.findByNameContaining(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vaccine> findByRecommendedAge(Integer ageInMonths) {
        return vaccineRepository.findByRecommendedAge(ageInMonths);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vaccine> findByRequired(Boolean isRequired) {
        return vaccineRepository.findByIsRequired(isRequired);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vaccine> findAll() {
        return vaccineRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteVaccine(Long id) {
        vaccineRepository.deleteById(id);
    }

    @Override
    @Transactional
    public VaccineSchedule saveVaccineSchedule(VaccineSchedule vaccineSchedule) {
        return vaccineScheduleRepository.save(vaccineSchedule);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VaccineSchedule> findVaccineSchedulesByChildId(Long childId) {
        return vaccineScheduleRepository.findByChildId(childId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VaccineSchedule> findUpcomingVaccineSchedules(Long childId) {
        LocalDate today = LocalDate.now();
        LocalDate threeMonthsLater = today.plusMonths(3);
        
        List<VaccineSchedule> schedules = vaccineScheduleRepository.findByChildIdAndScheduledDateBetween(
                childId, today, threeMonthsLater);
        
        // Only return scheduled (not completed, missed, or cancelled) vaccines
        return schedules.stream()
                .filter(schedule -> schedule.getStatus() == VaccineSchedule.Status.SCHEDULED)
                .collect(Collectors.toList());
    }
}
