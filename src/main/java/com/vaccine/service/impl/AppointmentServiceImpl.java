package com.vaccine.service.impl;

import com.vaccine.model.Appointment;
import com.vaccine.model.Child;
import com.vaccine.model.Payment;
import com.vaccine.model.User;
import com.vaccine.repository.AppointmentRepository;
import com.vaccine.repository.PaymentRepository;
import com.vaccine.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    @Transactional
    public Appointment createAppointment(Appointment appointment) {
        appointment.setStatus(Appointment.Status.SCHEDULED);
        return appointmentRepository.save(appointment);
    }

    @Override
    @Transactional
    public Appointment updateAppointment(Appointment appointment) {
        if (!appointmentRepository.existsById(appointment.getId())) {
            throw new RuntimeException("Appointment not found!");
        }
        return appointmentRepository.save(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Appointment> findById(Long id) {
        return appointmentRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Appointment> findByChild(Child child) {
        return appointmentRepository.findByChild(child);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Appointment> findByParentId(Long parentId) {
        return appointmentRepository.findByParentId(parentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Appointment> findByStaff(User staff) {
        return appointmentRepository.findByStaff(staff);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Appointment> findByStatus(Appointment.Status status) {
        return appointmentRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Appointment> findByDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return appointmentRepository.findByAppointmentDateTimeBetween(startDateTime, endDateTime);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    @Override
    @Transactional
    public void cancelAppointment(Long id) {
        appointmentRepository.findById(id).ifPresent(appointment -> {
            appointment.setStatus(Appointment.Status.CANCELLED);
            appointmentRepository.save(appointment);
            
            // Also update any associated payment
            paymentRepository.findByAppointmentId(id).ifPresent(payment -> {
                if (payment.getStatus() == Payment.PaymentStatus.PENDING) {
                    payment.setStatus(Payment.PaymentStatus.REFUNDED);
                    paymentRepository.save(payment);
                }
            });
        });
    }

    @Override
    @Transactional
    public Payment processPayment(Long appointmentId, Payment payment) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found!"));
        
        payment.setAppointment(appointment);
        
        // Check if payment already exists
        Optional<Payment> existingPayment = paymentRepository.findByAppointmentId(appointmentId);
        if (existingPayment.isPresent()) {
            Payment oldPayment = existingPayment.get();
            oldPayment.setAmount(payment.getAmount());
            oldPayment.setStatus(payment.getStatus());
            oldPayment.setPaymentMethod(payment.getPaymentMethod());
            oldPayment.setTransactionId(payment.getTransactionId());
            oldPayment.setNotes(payment.getNotes());
            return paymentRepository.save(oldPayment);
        } else {
            return paymentRepository.save(payment);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Payment> findPaymentByAppointmentId(Long appointmentId) {
        return paymentRepository.findByAppointmentId(appointmentId);
    }
}
