package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.SalaryvariableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SalaryVariableRepository extends JpaRepository<SalaryvariableEntity, Integer> {


    Optional<SalaryvariableEntity> findByCode(String code);
}
