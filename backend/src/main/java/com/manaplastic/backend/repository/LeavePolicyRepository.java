package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.LeavepolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeavePolicyRepository extends JpaRepository<LeavepolicyEntity, Integer> {
    @Query("SELECT p FROM LeavepolicyEntity p" +
            " WHERE p.leavetype = :leaveType " +
            "AND :thamNien >= p.minyearsservice " +
            "AND (:thamNien < p.maxyearsservice " +
            "OR p.maxyearsservice IS NULL) " +
            "AND (p.jobtype = :jobType O" +
            "R p.jobtype IS NULL) ORDER BY p.jobtype DESC")
    List<LeavepolicyEntity> findPolicyMatches(
            @Param("leaveType") LeavepolicyEntity.LeaveType leaveType,
            @Param("thamNien") int thamNien,
            @Param("jobType") String jobType
    );
}
