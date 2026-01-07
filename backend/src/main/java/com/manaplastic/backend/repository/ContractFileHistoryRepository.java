package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.ContractfilehistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractFileHistoryRepository extends JpaRepository<ContractfilehistoryEntity, Integer> {
    List<ContractfilehistoryEntity> findByContractIdOrderByArchivedAtDesc(Integer contractId); // contract.id
}
