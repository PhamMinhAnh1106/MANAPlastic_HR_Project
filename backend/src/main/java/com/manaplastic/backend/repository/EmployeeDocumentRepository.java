package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.EmployeeDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmployeeDocumentRepository extends JpaRepository<EmployeeDocumentEntity, Integer> ,
        JpaSpecificationExecutor<EmployeeDocumentEntity> {

    List<EmployeeDocumentEntity> findByStatus(EmployeeDocumentEntity.DocumentStatus status);


    @Query("SELECT COUNT(d) > 0 " +
            "FROM EmployeeDocumentEntity d " +
            "WHERE d.userID.id = :userId " +
            "AND d.documentType = :type " +
            "AND d.status = :status " +
            "AND :checkDate BETWEEN d.issueDate AND d.expiryDate")
    boolean isDocumentActive(
            @Param("userId") Integer userId,
            @Param("checkDate") LocalDate checkDate,
            @Param("type") EmployeeDocumentEntity.DocumentType type,
            @Param("status") EmployeeDocumentEntity.DocumentStatus status
    );
}