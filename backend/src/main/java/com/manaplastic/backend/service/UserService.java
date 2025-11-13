package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.UpdateAccountDTO;
import com.manaplastic.backend.DTO.UserFilterCriteria;
import com.manaplastic.backend.DTO.UserProfileDTO;
import com.manaplastic.backend.DTO.UpdateSelfIn4DTO;
import com.manaplastic.backend.entity.DepartmentEntity;
import com.manaplastic.backend.entity.RoleEntity;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.filters.UserFilter;
import com.manaplastic.backend.repository.DepartmentRepository;
import com.manaplastic.backend.repository.RoleRepository;
import com.manaplastic.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserProfileDTO updateUserProfile(int userId, UpdateSelfIn4DTO request) {

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

    @Transactional
    public void changeUserPassword(UserEntity currentUser, String oldPassword, String newPassword) {
        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu cũ không chính xác.");
        }

        if (passwordEncoder.matches(newPassword, currentUser.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu mới không được trùng với mật khẩu cũ.");
        }

        currentUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(currentUser);
    }

    public List<UserProfileDTO> filterUsersList(UserFilterCriteria criteria, Pageable pageable) {

        Specification<UserEntity> spec = UserFilter.withCriteria(criteria);
        Page<UserEntity> userPage = userRepository.findAll(spec, pageable);
        List<UserEntity> userEntities = userPage.getContent(); // chỉ lấy key "content"

        // Mapping List<UserEntity> sang List<UserProfileDTO>
        return userEntities.stream()
                .map(this::mapToUserProfileDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserProfileDTO updateAccount(int userId, UpdateAccountDTO request, UserEntity currentUser) {
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUser.getRoleID().getRolename());
        boolean isHR = "HR".equalsIgnoreCase(currentUser.getRoleID().getRolename());


        if (!isAdmin && !isHR) {
            throw new AccessDeniedException("Bạn không có quyền cập nhật thông tin tài khoản này.");
        }
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

        if (request.getDepartmentID() != null) {
                DepartmentEntity department = departmentRepository.findById(request.getDepartmentID())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng ban."));
                userToUpdate.setDepartmentID(department);

        }

        if (request.getStatus() != null && (isAdmin || isHR)) {
            userToUpdate.setStatus(request.getStatus());
        }

        if (request.getRoleID() != null) {
            if (isAdmin) {
                RoleEntity role = roleRepository.findById(request.getRoleID())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò."));
                userToUpdate.setRoleID(role);
            }
            else if (isHR) {
                throw new AccessDeniedException("HR không có quyền sửa vai trò của người dùng.");
            }
        }

        if(request.getSkillGrade()!=null && (isAdmin || isHR)){
            userToUpdate.setSkillGrade(request.getSkillGrade());
        }

        UserEntity updatedUser = userRepository.save(userToUpdate);

        return mapToUserProfileDTO(updatedUser);
    }

    public UserProfileDTO getUserDetailsById(int userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));

        return mapToUserProfileDTO(userEntity);
    }

    private UserProfileDTO mapToUserProfileDTO(UserEntity entity) {

        // Lấy mã phon ban nếu giả sử user đó chưa có phòng ban
        Integer deptId = (entity.getDepartmentID() != null)
                ? entity.getDepartmentID().getId()
                : null;

        return UserProfileDTO.builder()
                .userID(entity.getId())
                .username(entity.getUsername())
                .fullname(entity.getFullname())
                .email(entity.getEmail())
                .phonenumber(entity.getPhonenumber())
                .address(entity.getAddress())
                .roleName(entity.getRoleID().getRolename())
                .departmentID(deptId)
                .cccd(entity.getCccd())
                .gender(entity.getGender())
                .birth(entity.getBirth())
                .bankAccount(entity.getBankaccount())
                .bankName(entity.getBankname())
                .status(entity.getStatus())
                .skillGrade(entity.getSkillGrade())
                .build();
    }
}
