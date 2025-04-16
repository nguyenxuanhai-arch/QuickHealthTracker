package com.vaccine.repository;

import com.vaccine.model.Appointment;
import com.vaccine.model.Feedback;
import com.vaccine.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByUser(User user);
    
    List<Feedback> findByUserId(Long userId);
    
    List<Feedback> findByAppointment(Appointment appointment);
    
    List<Feedback> findByAppointmentId(Long appointmentId);
    
    List<Feedback> findByType(Feedback.FeedbackType type);
    
    List<Feedback> findByIsPublic(Boolean isPublic);
    
    @Query("SELECT AVG(f.rating) FROM Feedback f")
    Double averageRating();
    
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.type = ?1")
    Double averageRatingByType(Feedback.FeedbackType type);
}
