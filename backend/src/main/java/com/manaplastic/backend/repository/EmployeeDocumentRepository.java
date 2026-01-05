package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.EmployeeDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmployeeDocumentRepository extends JpaRepository<EmployeeDocumentEntity, Integer> ,
        JpaSpecificationExecutor<EmployeeDocumentEntity> {

    List<EmployeeDocumentEntity> findByStatus(EmployeeDocumentEntity.DocumentStatus status);

    @Query("SELECT d FROM EmployeeDocumentEntity d WHERE d.userID.id = :userId " +
            "AND d.documentType = :type " +
            "AND d.status = 'APPROVED' " +
            "AND (d.expiryDate IS NULL OR d.expiryDate >= :checkDate)")
    List<EmployeeDocumentEntity> findValidDocuments(Integer userId, EmployeeDocumentEntity.DocumentType type, LocalDate checkDate);

    List<EmployeeDocumentEntity> findByUserID_Id(Integer userId);
}