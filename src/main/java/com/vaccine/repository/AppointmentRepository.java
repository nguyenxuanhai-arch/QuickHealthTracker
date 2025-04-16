package com.vaccine.repository;

import com.vaccine.model.Appointment;
import com.vaccine.model.Child;
import com.vaccine.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByChild(Child child);
    
    List<Appointment> findByChildId(Long childId);
    
    List<Appointment> findByStaff(User staff);
    
    List<Appointment> findByStaffId(Long staffId);
    
    List<Appointment> findByStatus(Appointment.Status status);
    
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDateTime BETWEEN ?1 AND ?2")
    List<Appointment> findByAppointmentDateTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    @Query("SELECT a FROM Appointment a JOIN a.child c JOIN c.parent p WHERE p.id = ?1")
    List<Appointment> findByParentId(Long parentId);
    
    @Query("SELECT a FROM Appointment a JOIN a.child c JOIN c.parent p WHERE p.id = ?1 AND a.status = ?2")
    List<Appointment> findByParentIdAndStatus(Long parentId, Appointment.Status status);
}
