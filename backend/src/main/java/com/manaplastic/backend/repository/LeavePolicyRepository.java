package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.LeavepolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeavePolicyRepository extends JpaRepository<LeavepolicyEntity, Integer> {
    @Query("SELECT p FROM LeavepolicyEntity p " +
            "WHERE p.leaveType = :leaveType " +
            "AND :thamNien >= p.minYearsService " +
            "AND (:thamNien < p.maxYearsService OR p.maxYearsService IS NULL) " +
            "AND (p.jobType = :jobType OR p.jobType IS NULL) " +
            "ORDER BY p.jobType DESC")// Ưu tiên chính sách có job_type cụ thể (NORMAL/DANGER) hơn chính sách chung (NULL)
    List<LeavepolicyEntity> findPolicyMatches(
            @Param("leaveType") LeavepolicyEntity.LeaveType leaveType,
            @Param("thamNien") int thamNien,
            @Param("jobType") String jobType
    );
}
