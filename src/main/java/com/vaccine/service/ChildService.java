package com.vaccine.service;

import com.vaccine.model.Child;
import com.vaccine.model.User;

import java.util.List;
import java.util.Optional;

public interface ChildService {

    Child createChild(Child child, User parent);
    
    Child updateChild(Child child);
    
    Optional<Child> findById(Long id);
    
    List<Child> findByParent(User parent);
    
    List<Child> findByParentId(Long parentId);
    
    List<Child> findAll();
    
    void deleteChild(Long id);
    
    // Schedule vaccines for a child based on their age
    void scheduleVaccinesForChild(Child child);
}
