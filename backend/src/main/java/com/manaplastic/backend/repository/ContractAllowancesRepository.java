package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.ContractEntity;
import com.manaplastic.backend.entity.ContractallowanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ContractAllowancesRepository extends JpaRepository<ContractallowanceEntity, Integer> {
    List<ContractallowanceEntity> findByContractID(ContractEntity contractID);

    void deleteByContractID(ContractEntity contract);


}
