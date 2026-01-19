package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.account.UpdateAccountDTO;
import com.manaplastic.backend.DTO.account.UserSuggestionDTO;
import com.manaplastic.backend.DTO.criteria.UserFilterCriteria;
import com.manaplastic.backend.DTO.account.UserProfileDTO;
import com.manaplastic.backend.DTO.account.UpdateSelfIn4DTO;
import com.manaplastic.backend.entity.DepartmentEntity;
import com.manaplastic.backend.entity.RoleEntity;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.filters.UserFilter;
import com.manaplastic.backend.repository.DepartmentRepository;
import com.manaplastic.backend.repository.RoleRepository;
import com.manaplastic.backend.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
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

        if (request.getTaxCode() != null && !request.getTaxCode().isEmpty()) {
            String taxCodeClean = request.getTaxCode().trim();

            if (taxCodeClean.length() != 10 && taxCodeClean.length() != 13) {
                throw new IllegalArgumentException("Mã số thuế không hợp lệ (phải là 10 hoặc 13 số).");
            }

            if (!taxCodeClean.equals(userToUpdate.getTaxCode())) {
                if (userRepository.existsByTaxCode(taxCodeClean)) {
                    throw new IllegalArgumentException("Mã số thuế này đã được sử dụng bởi nhân viên khác.");
                }
                userToUpdate.setTaxCode(taxCodeClean);
            }
        }

        if (request.getSocialInsuranceNumber() != null && !request.getSocialInsuranceNumber().isEmpty()) {
            String bhxhClean = request.getSocialInsuranceNumber().trim();

            if (bhxhClean.length() != 10) {
                throw new IllegalArgumentException("Số BHXH không hợp lệ (phải đúng 10 số).");
            }

            if (!bhxhClean.equals(userToUpdate.getSocialInsuranceNumber())) {
                if (userRepository.existsBySocialInsuranceNumber(bhxhClean)) {
                    throw new IllegalArgumentException("Số BHXH này đã được sử dụng bởi nhân viên khác.");
                }
                userToUpdate.setSocialInsuranceNumber(bhxhClean);
            }
        }

        if (request.getPhonenumber() != null) {

            if (!request.getPhonenumber().matches("\\d{10,11}")) {
                throw new IllegalArgumentException("Số điện thoại không hợp lệ (10 hoặc 11 số).");
            }

            userToUpdate.setPhonenumber(request.getPhonenumber());
        }

        if (request.getAddress() != null) {
            userToUpdate.setAddress(request.getAddress());
        }


        if (request.getEmail() != null && !request.getEmail().isEmpty()
                && !request.getEmail().equals(userToUpdate.getEmail())) {

            if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new IllegalArgumentException("Email có định dạng không hợp lệ.");
            }

            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email đã được sử dụng bởi tài khoản khác.");
            }

            userToUpdate.setEmail(request.getEmail());
        }

        if (request.getGender() != null) {
            userToUpdate.setGender(request.getGender());
        }

        if (request.getBirth() != null) {

            if (Period.between(request.getBirth(), LocalDate.now()).getYears() < 18) {
                throw new IllegalArgumentException("Bạn chưa đủ 18 tuổi.");
            }

            userToUpdate.setBirth(request.getBirth());
        }

        if (request.getBankAccount() != null) {
            userToUpdate.setBankaccount(request.getBankAccount());
        }

        if (request.getBankName() != null) {
            userToUpdate.setBankname(request.getBankName());
        }


        if (request.getCccd() != null) {
            if (request.getCccd().trim().length() != 12) {
                throw new IllegalArgumentException("CCCD không hợp lệ (phải đúng 12 số).");
            }
            if (!request.getCccd().equals(userToUpdate.getCccd())) {
                if (userRepository.existsByCccd(request.getCccd())) {
                    throw new IllegalArgumentException("CCCD này đã được sử dụng bởi tài khoản khác.");
                }
                userToUpdate.setCccd(request.getCccd());
            }
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

    //    public List<UserProfileDTO> filterUsersList(UserFilterCriteria criteria, Pageable pageable) {
//
//        Specification<UserEntity> spec = UserFilter.withCriteria(criteria);
//        Page<UserEntity> userPage = userRepository.findAll(spec, pageable);
//        List<UserEntity> userEntities = userPage.getContent(); // chỉ lấy key "content"
//
//        // Mapping List<UserEntity> sang List<UserProfileDTO>
//        return userEntities.stream()
//                .map(this::mapToUserProfileDTO)
//                .collect(Collectors.toList());
//    }
    public Page<UserProfileDTO> filterUsersList(UserFilterCriteria criteria, Pageable pageable) {
        Specification<UserEntity> spec = UserFilter.withCriteria(criteria);
        Page<UserEntity> userPage = userRepository.findAll(spec, pageable);

        return userPage.map(this::mapToUserProfileDTO);
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

        if (request.getTaxCode() != null && !request.getTaxCode().isEmpty()) {
            String taxCodeClean = request.getTaxCode().trim();

            if (taxCodeClean.length() != 10 && taxCodeClean.length() != 13) {
                throw new IllegalArgumentException("Mã số thuế không hợp lệ (phải là 10 hoặc 13 số).");
            }

            if (!taxCodeClean.equals(userToUpdate.getTaxCode())) {
                if (userRepository.existsByTaxCode(taxCodeClean)) {
                    throw new IllegalArgumentException("Mã số thuế này đã được sử dụng bởi nhân viên khác.");
                }
                userToUpdate.setTaxCode(taxCodeClean);
            }
        }

        if (request.getSocialInsuranceNumber() != null && !request.getSocialInsuranceNumber().isEmpty()) {
            String bhxhClean = request.getSocialInsuranceNumber().trim();

            if (bhxhClean.length() != 10) {
                throw new IllegalArgumentException("Số BHXH không hợp lệ (phải đúng 10 số).");
            }

            if (!bhxhClean.equals(userToUpdate.getSocialInsuranceNumber())) {
                if (userRepository.existsBySocialInsuranceNumber(bhxhClean)) {
                    throw new IllegalArgumentException("Số BHXH này đã được sử dụng bởi nhân viên khác.");
                }
                userToUpdate.setSocialInsuranceNumber(bhxhClean);
            }
        }

        if (request.getPhonenumber() != null) {

            if (!request.getPhonenumber().matches("\\d{10,11}")) {
                throw new IllegalArgumentException("Số điện thoại không hợp lệ (10 hoặc 11 số).");
            }

            userToUpdate.setPhonenumber(request.getPhonenumber());
        }

        if (request.getAddress() != null) {
            userToUpdate.setAddress(request.getAddress());
        }


        if (request.getEmail() != null && !request.getEmail().isEmpty()
                && !request.getEmail().equals(userToUpdate.getEmail())) {

            if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new IllegalArgumentException("Email có định dạng không hợp lệ.");
            }

            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email đã được sử dụng bởi tài khoản khác.");
            }
            userToUpdate.setEmail(request.getEmail());
        }

        if (request.getGender() != null) {
            userToUpdate.setGender(request.getGender());
        }

        if (request.getBirth() != null) {

            if (Period.between(request.getBirth(), LocalDate.now()).getYears() < 18) {
                throw new IllegalArgumentException("Bạn chưa đủ 18 tuổi.");
            }

            userToUpdate.setBirth(request.getBirth());
        }

        if (request.getBankAccount() != null) {
            userToUpdate.setBankaccount(request.getBankAccount());
        }

        if (request.getBankName() != null) {
            userToUpdate.setBankname(request.getBankName());
        }

        if (request.getCccd() != null) {
            if (request.getCccd().trim().length() != 12) {
                throw new IllegalArgumentException("CCCD không hợp lệ (phải đúng 12 số).");
            }
            if (!request.getCccd().equals(userToUpdate.getCccd())) {
                if (userRepository.existsByCccd(request.getCccd())) {
                    throw new IllegalArgumentException("CCCD này đã được sử dụng bởi tài khoản khác.");
                }
                userToUpdate.setCccd(request.getCccd());
            }
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
            } else if (isHR) {
                throw new AccessDeniedException("HR không có quyền sửa vai trò của người dùng.");
            }
        }

        if (request.getSkillGrade() != null && (isAdmin || isHR)) {
            userToUpdate.setSkillGrade(request.getSkillGrade());
        }

        if (request.getHireDate() != null && (isAdmin || isHR)) {
            userToUpdate.setHiredate(request.getHireDate());
        }

        if (request.getJobType() != null && (isAdmin || isHR)) {
            userToUpdate.setJobtype(request.getJobType());
        }

        UserEntity updatedUser = userRepository.save(userToUpdate);

        return mapToUserProfileDTO(updatedUser);
    }

    public UserProfileDTO getUserDetailsById(int userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));

        return mapToUserProfileDTO(userEntity);
    }

    // search auto advice
    public List<UserSuggestionDTO> searchUsers(UserEntity currentUser, String keyword) {

        Integer filterDeptId = null;

        //  CHECK QUYỀN: Xác định xem có cần lọc theo phòng ban không
        String roleName = (currentUser.getRoleID() != null) ? currentUser.getRoleID().getRolename() : "";

        // Nếu KHÔNG phải HR/Admin thì bắt buộc phải lấy ID phòng ban hiện tại
        if (!"HR".equalsIgnoreCase(roleName) && !"Admin".equalsIgnoreCase(roleName)) {
            if (currentUser.getDepartmentID() == null) {
                return new ArrayList<>();
            }
            filterDeptId = currentUser.getDepartmentID().getId();
        }

        Integer finalDeptId = filterDeptId;

        Specification<UserEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();


            if (keyword != null && !keyword.trim().isEmpty()) {
                String likePattern = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("username")), likePattern));
            }

            if (finalDeptId != null) {
                predicates.add(cb.equal(root.get("departmentID").get("id"), finalDeptId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return userRepository.findAll(spec).stream()
                .map(user -> new UserSuggestionDTO(user.getId(),user.getUsername(), user.getFullname()))
                .collect(Collectors.toList());
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
                .taxCode(entity.getTaxCode())
                .socialInsuranceNumber(entity.getSocialInsuranceNumber())
                .phonenumber(entity.getPhonenumber())
                .address(entity.getAddress())
                .roleName(entity.getRoleID().getRolename())
                .departmentID(deptId)
                .departmentName(entity.getDepartmentID().getDepartmentname())
                .cccd(entity.getCccd())
                .gender(entity.getGender())
                .birth(entity.getBirth())
                .bankAccount(entity.getBankaccount())
                .bankName(entity.getBankname())
                .status(entity.getStatus())
                .skillGrade(entity.getSkillGrade())
                .hireDate(entity.getHiredate())
                .jobType(entity.getJobtype())
                .build();
    }
}
