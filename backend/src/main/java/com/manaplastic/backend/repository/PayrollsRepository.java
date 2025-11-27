package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.PayrollEntity;
import com.manaplastic.backend.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollsRepository extends JpaRepository<PayrollEntity, Integer> {
    Optional<PayrollEntity> findByUserIDAndPayperiod(UserEntity userID, String payperiod);

    @Transactional
    void deleteByPayperiod(String payperiod);

    boolean existsByPayperiod(String period);

    List<PayrollEntity> findByPayperiod(String period);
}
