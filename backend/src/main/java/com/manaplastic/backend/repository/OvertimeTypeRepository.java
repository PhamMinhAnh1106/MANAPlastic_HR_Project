package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.OvertimetypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OvertimeTypeRepository extends JpaRepository<OvertimetypeEntity, Integer> {
    boolean existsByOtCode(String otCode);
    boolean existsByOtCodeAndIdNot(String otCode, Integer id);
}
