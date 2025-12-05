package com.manaplastic.backend.constant.customAnotation;


import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.repository.UserRepository;
import com.manaplastic.backend.service.CheckPermissionService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    private final CheckPermissionService checkPermissionService;
    private final UserRepository userRepo;

    @Before("@annotation(requiredPermission)")
    public void validatePermission(RequiredPermission requiredPermission) {
        // Lấy thông tin User hiện tại từ Security Context (JWT/Session)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new RuntimeException("Unauthorized: Bạn chưa đăng nhập");
        }

        String currentUsername = auth.getName();
        // Lấy UserID từ DB (nên cache lại để tối ưu hiệu năng)
        UserEntity currentUser = userRepo.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found: " + currentUsername));

        // Lấy mã quyền yêu cầu từ Annotation và logiccal
        String[] requiredPermissions = requiredPermission.value();
        LogicalOperator logic = requiredPermission.logic();

        boolean isAllowed;
        if (logic == LogicalOperator.OR) {
            isAllowed = false;
            for (String code : requiredPermissions) {
                if (checkPermissionService.checkPermission(currentUser.getId(), code)) {
                    isAllowed = true;
                    break;
                }
            }
        } else {
            isAllowed = true;
            for (String code : requiredPermissions) {
                if (!checkPermissionService.checkPermission(currentUser.getId(), code)) {
                    isAllowed = false;
                    break;
                }
            }
        }

        if (!isAllowed) {
            throw new RuntimeException("Access Denied: Bạn thiếu quyền truy cập. Yêu cầu một trong các quyền: " + String.join(", ", requiredPermissions));
        }
    }
}