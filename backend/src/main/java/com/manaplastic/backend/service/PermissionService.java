package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.account.PermissionDTO;
import com.manaplastic.backend.DTO.criteria.PermissionFilterCriteria;
import com.manaplastic.backend.DTO.account.UpdateUserPermissionDTO;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.repository.PermissionRepository;
import com.manaplastic.backend.repository.UserPermissionRepository;
import com.manaplastic.backend.repository.UserRepository;
import com.manaplastic.backend.entity.PermissionEntity;
import com.manaplastic.backend.entity.UserpermissionEntity;   // Đã sửa tên class
import com.manaplastic.backend.entity.UserpermissionEntityId; // Đã sửa tên class key
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserPermissionRepository userPermissionRepository;

    @Autowired
    private UserRepository userRepository;

    // Lấy danh sách tất cả quyền kèm trạng thái của 1 User cụ thể
//    public Page<PermissionDTO> getAllPermissionsForUser(Integer userId, Pageable pageable) {
//        return getAllPermissionsForUserInternal(userId, pageable);
//    }
    public Page<PermissionDTO> getAllPermissionsForUser(String username, PermissionFilterCriteria criteria, Pageable pageable) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại với username: " + username));

        return getAllPermissionsForUserInternal(user.getId(), criteria, pageable);
    }

    // Hàm xử lý logic chính (Private)
    private Page<PermissionDTO> getAllPermissionsForUserInternal(Integer userId, PermissionFilterCriteria criteria, Pageable pageable) {

        // A. Lấy dữ liệu thô từ Database
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Giả định RoleEntity có getId(), hãy sửa lại getter nếu tên khác
        Integer roleId = user.getRoleID().getId();

        List<PermissionEntity> allPermissions = permissionRepository.findAll();
        List<UserpermissionEntity> userConfigs = userPermissionRepository.findByUserID_Id(userId);
        List<Integer> rolePermissionIds = permissionRepository.findPermissionIdsByRoleId(roleId);

        // B. Map dữ liệu sang DTO và thực hiện LỌC (Filtering)
        List<PermissionDTO> filteredList = allPermissions.stream()
                .map(perm -> {
                    // Logic xác định trạng thái
                    boolean isEnabledByRole = rolePermissionIds.contains(perm.getId());

                    Integer activeStatus = userConfigs.stream()
                            .filter(uc -> uc.getId().getPermissionID().equals(perm.getId()))
                            .findFirst()
                            .map(uc -> (uc.getActivepermission() != null && uc.getActivepermission()) ? 1 : 0)
                            .orElse(null);

                    return PermissionDTO.builder()
                            .permissionId(perm.getId())
                            .permissionCode(perm.getPermissionname())
                            .description(perm.getDescription())
                            .enabledByRole(isEnabledByRole)
                            .activePermission(activeStatus)
                            .build();
                })
                .filter(dto -> {
                    // Lọc theo Permission ID (Ưu tiên cao nhất, nếu tìm ID thì chỉ trả về ID đó)
                    if (criteria.getPermissionId() != null) {
                        if (!dto.getPermissionId().equals(criteria.getPermissionId())) {
                            return false;
                        }
                    }

                    // Lọc theo Permission Code (So sánh chính xác, không phân biệt hoa thường)
                    if (criteria.getPermissionCode() != null && !criteria.getPermissionCode().isEmpty()) {
                        if (!dto.getPermissionCode().equalsIgnoreCase(criteria.getPermissionCode())) {
                            return false;
                        }
                    }

                    // Lọc theo Keyword (Tìm tương đối trong Code hoặc Description)
                    if (criteria.getKeyword() != null && !criteria.getKeyword().isEmpty()) {
                        String keyword = criteria.getKeyword().toLowerCase();
                        boolean matchCode = dto.getPermissionCode().toLowerCase().contains(keyword);
                        boolean matchDesc = dto.getDescription() != null && dto.getDescription().toLowerCase().contains(keyword);
                        if (!matchCode && !matchDesc) return false;
                    }

                    // Lọc theo Active Status
                    if (criteria.getActiveStatus() != null) {
                        if (dto.getActivePermission() == null || !dto.getActivePermission().equals(criteria.getActiveStatus())) {
                            return false;
                        }
                    }

                    // Lọc Override
                    if (Boolean.TRUE.equals(criteria.getOnlyOverride())) {
                        if (dto.getActivePermission() == null) return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredList.size());

        List<PermissionDTO> pagedList;
        if (start > filteredList.size()) {
            pagedList = Collections.emptyList();
        } else {
            pagedList = filteredList.subList(start, end);
        }

        return new PageImpl<>(pagedList, pageable, filteredList.size());
    }

    // Cập nhật quyền cho User (Thêm/Sửa)
    @Transactional
    public void updateUserPermission(UpdateUserPermissionDTO dto) {
        UserEntity user = null;
        if (dto.getUserId() != null) {
            user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy User với ID: " + dto.getUserId()));
        } else if (dto.getUsername() != null && !dto.getUsername().isEmpty()) {
            user = userRepository.findByUsername(dto.getUsername())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy User với Username: " + dto.getUsername()));
        } else {
            throw new RuntimeException("Vui lòng cung cấp User ID hoặc Username để thực hiện.");
        }

        PermissionEntity permission = permissionRepository.findById(dto.getPermissionId())
                .orElseThrow(() -> new RuntimeException("Quyền (Permission) không tồn tại với ID: " + dto.getPermissionId()));

        UserpermissionEntityId key = new UserpermissionEntityId();
        key.setUserID(user.getId());          // Lấy ID từ user đã tìm được
        key.setPermissionID(permission.getId()); // Lấy ID từ permission

        // Tìm bản ghi hiện tại hoặc tạo mới
        UserpermissionEntity userPerm = userPermissionRepository.findById(key)
                .orElse(new UserpermissionEntity());


        if (userPerm.getId() == null) {
            userPerm.setId(key);
            userPerm.setUserID(user);          // Quan trọng: Set object UserEntity cho @MapsId("userID")
            userPerm.setPermissionID(permission); // Quan trọng: Set object PermissionEntity cho @MapsId("permissionID")
        }

        boolean isActive = (dto.getActivePermission() != null && dto.getActivePermission() == 1);
        userPerm.setActivepermission(isActive);

        userPermissionRepository.save(userPerm);
    }

    // Xóa cấu hình quyền (Reset về mặc định Role)
//    @Transactional
//    public void resetUserPermission(Integer userId, Integer permissionId) {
//        UserpermissionEntityId key = new UserpermissionEntityId();
//        key.setUserID(userId);
//        key.setPermissionID(permissionId);
//
//        if (userPermissionRepository.existsById(key)) {
//            userPermissionRepository.deleteById(key);
//        }
//    }
    @Transactional
    public void resetUserPermission(String username, Integer permissionId) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại: " + username));

        resetUserPermissionInternal(user.getId(), permissionId);
    }
//    @Transactional
//    public void resetUserPermission(Integer userId, Integer permissionId) {
//        resetUserPermissionInternal(userId, permissionId);
//    }
    private void resetUserPermissionInternal(Integer userId, Integer permissionId) {
        UserpermissionEntityId key = new UserpermissionEntityId();
        key.setUserID(userId);
        key.setPermissionID(permissionId);

        if (userPermissionRepository.existsById(key)) {
            userPermissionRepository.deleteById(key);
        }
    }

}