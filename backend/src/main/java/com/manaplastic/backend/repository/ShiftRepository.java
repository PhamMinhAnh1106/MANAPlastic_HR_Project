package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.ShiftEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftRepository extends JpaRepository<ShiftEntity, Integer> {
}
