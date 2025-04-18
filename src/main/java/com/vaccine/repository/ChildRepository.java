package com.vaccine.repository;

import com.vaccine.model.Child;
import com.vaccine.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChildRepository extends JpaRepository<Child, Long> {
    List<Child> findByParent(User parent);
    
    List<Child> findByParentId(Long parentId);
}
