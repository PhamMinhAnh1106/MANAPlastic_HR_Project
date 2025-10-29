package com.manaplastic.backend.Repository;

import com.manaplastic.backend.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    //  tạo câu lệnh "SELECT * FROM users WHERE username = ?" tựdđong
    Optional<UserEntity> findByUsername(String username);
}
