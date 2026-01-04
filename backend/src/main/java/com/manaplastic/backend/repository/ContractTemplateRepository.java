package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.ContractTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractTemplateRepository extends JpaRepository<ContractTemplateEntity, Integer> {
    Optional<ContractTemplateEntity> findFirstByTypeAndIsActiveTrue(String type);
}