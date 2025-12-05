package com.manaplastic.backend.service;

import com.manaplastic.backend.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CheckPermissionService {

    private final PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public boolean checkPermission(Integer userId, String permissionCode) {
        Boolean isSystemActive = permissionRepository.getPermissionActiveStatus(permissionCode);

        if (isSystemActive == null || !isSystemActive) {
            return false;
        }
        // Black list
        if (permissionRepository.countUserDeny(userId, permissionCode) > 0) {
            return false;
        }
        //White list
        if (permissionRepository.countUserAllow(userId, permissionCode) > 0) {
            return true;
        }
        // Mặc định
        return permissionRepository.countRoleAllow(userId, permissionCode) > 0;
    }
}
