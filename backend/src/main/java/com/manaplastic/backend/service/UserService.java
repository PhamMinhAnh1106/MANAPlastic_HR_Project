package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.UserProfileDTO;
import com.manaplastic.backend.DTO.UserUpdatein4DTO;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

//    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserProfileDTO updateUserProfile(int userId, UserUpdatein4DTO request) {

        UserEntity userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));

        if (request.getFullname() != null && !request.getFullname().isEmpty()) {
            userToUpdate.setFullname(request.getFullname());
        }

        if (request.getPhonenumber() != null) {
            userToUpdate.setPhonenumber(request.getPhonenumber());
        }

        if (request.getAddress() != null) {
            userToUpdate.setAddress(request.getAddress());
        }


        if (request.getEmail() != null && !request.getEmail().isEmpty()
                && !request.getEmail().equals(userToUpdate.getEmail())) {

            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email đã được sử dụng bởi tài khoản khác.");
            }
            userToUpdate.setEmail(request.getEmail());
        }

        if (request.getGender() != null) {
            userToUpdate.setGender(request.getGender());
        }

        if (request.getBirth() != null) {
            userToUpdate.setBirth(request.getBirth());
        }

        if (request.getBankAccount() != null) {
            userToUpdate.setBankaccount(request.getBankAccount());
        }

        if (request.getBankName() != null) {
            userToUpdate.setBankname(request.getBankName());
        }


        if (request.getCccd() != null) {
            userToUpdate.setCccd(Long.valueOf(request.getCccd()));
        }


        UserEntity updatedUser = userRepository.save(userToUpdate);

        return mapToUserProfileDTO(updatedUser);
    }

    private UserProfileDTO mapToUserProfileDTO(UserEntity entity) {
        return UserProfileDTO.builder()
                .userID(entity.getId())
                .username(entity.getUsername())
                .fullname(entity.getFullname())
                .email(entity.getEmail())
                .phonenumber(entity.getPhonenumber())
                .address(entity.getAddress())
                .roleName(entity.getRoleID().getRolename())
                .departmentID(entity.getDepartmentID().getId())
                .cccd(entity.getCccd())
                .gender(entity.getGender())
                .birth(entity.getBirth())
                .bankAccount(entity.getBankaccount())
                .bankName(entity.getBankname())
                .build();
    }
}
