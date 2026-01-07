package com.hrpayroll.employee.repository;

import com.hrpayroll.employee.entity.Designation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DesignationRepository extends JpaRepository<Designation, Long> {
    Optional<Designation> findByName(String name);
    List<Designation> findByActiveTrue();
    boolean existsByName(String name);
}

