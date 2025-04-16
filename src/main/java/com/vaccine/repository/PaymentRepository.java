package com.vaccine.repository;

import com.vaccine.model.Appointment;
import com.vaccine.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByAppointment(Appointment appointment);
    
    Optional<Payment> findByAppointmentId(Long appointmentId);
    
    List<Payment> findByStatus(Payment.PaymentStatus status);
    
    List<Payment> findByPaymentMethod(Payment.PaymentMethod paymentMethod);
    
    @Query("SELECT p FROM Payment p WHERE p.createdAt BETWEEN ?1 AND ?2")
    List<Payment> findByCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED' AND p.createdAt BETWEEN ?1 AND ?2")
    Double sumCompletedPaymentsBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
}
