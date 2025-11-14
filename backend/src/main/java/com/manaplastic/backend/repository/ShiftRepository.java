package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.ShiftEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShiftRepository extends JpaRepository<ShiftEntity, Integer> {
    Optional<ShiftEntity> findByShiftname(String shiftname);

    @Query("SELECT s FROM ShiftEntity s WHERE s.shiftname LIKE 'AL%' " +
            "OR s.shiftname LIKE 'SL%' " +
            "OR s.shiftname LIKE 'PL%' " +
            "OR s.shiftname LIKE 'ML%' " +
            "OR s.shiftname LIKE 'UL%'")
    List<ShiftEntity> findAllLeaveTypes();

}
