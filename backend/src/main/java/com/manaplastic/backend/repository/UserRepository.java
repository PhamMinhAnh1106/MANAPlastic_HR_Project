package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    //  tạo câu lệnh "SELECT * FROM users WHERE username = ?" tựdđong
    Optional<UserEntity> findByUsername(String username);
}
