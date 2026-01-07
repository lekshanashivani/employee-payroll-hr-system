package com.hrpayroll.employee.service;

import com.hrpayroll.employee.entity.Designation;
import com.hrpayroll.employee.repository.DesignationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Designation Service
 * 
 * Designations are managed ONLY by ADMIN.
 * HR can view designations but cannot edit salary rules.
 */
@Service
@Transactional
public class DesignationService {

    @Autowired
    private DesignationRepository designationRepository;

    public Designation createDesignation(Designation designation) {
        if (designationRepository.existsByName(designation.getName())) {
            throw new RuntimeException("Designation with this name already exists");
        }
        return designationRepository.save(designation);
    }

    public Designation updateDesignation(Long id, Designation updatedDesignation) {
        Designation designation = designationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Designation not found"));

        // Check if name is being changed and if it conflicts
        if (!designation.getName().equals(updatedDesignation.getName()) &&
            designationRepository.existsByName(updatedDesignation.getName())) {
            throw new RuntimeException("Designation with this name already exists");
        }

        designation.setName(updatedDesignation.getName());
        designation.setBaseSalary(updatedDesignation.getBaseSalary());
        designation.setTaxPercentage(updatedDesignation.getTaxPercentage());
        designation.setBonusPercentage(updatedDesignation.getBonusPercentage());
        designation.setActive(updatedDesignation.getActive());

        return designationRepository.save(designation);
    }

    public List<Designation> getAllActiveDesignations() {
        return designationRepository.findByActiveTrue();
    }

    public Designation getDesignationById(Long id) {
        return designationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Designation not found"));
    }

    public void deactivateDesignation(Long id) {
        Designation designation = designationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Designation not found"));
        designation.setActive(false);
        designationRepository.save(designation);
    }
}

