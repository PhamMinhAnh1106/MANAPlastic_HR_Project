package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.DepartmentEntity;
import com.manaplastic.backend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer>, JpaSpecificationExecutor<UserEntity> {

    //  tạo câu lệnh "SELECT * FROM users WHERE username = ?" tựdđong
    Optional<UserEntity> findByUsername(String username);

    Optional<Object> findByEmail(String email);

    List<UserEntity> findByDepartmentID(DepartmentEntity departmentId);
}
