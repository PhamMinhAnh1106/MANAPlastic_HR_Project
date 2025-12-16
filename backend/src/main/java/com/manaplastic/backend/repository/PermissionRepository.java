package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.PermissionEntity;
import com.manaplastic.backend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Integer> , JpaSpecificationExecutor<PermissionEntity> {
    /**
     * Check 1: Kiểm tra xem User có bị CHẶN (activepermission = 0) quyền này không?
     *
     * @return 1 nếu bị chặn, null hoặc 0 nếu không bị chặn.
     */
    @Query(value = """
                SELECT COUNT(*)
                FROM userpermissions up
                JOIN permissions p ON up.permissionID = p.permissionID
                WHERE up.userID = :userId 
                  AND p.permissionname = :permissionCode
                  AND up.activepermission = 0
            """, nativeQuery = true)
    int countUserDeny(Integer userId, String permissionCode);


    /**
     * Check 2: Kiểm tra xem User có được CẤP RIÊNG (activepermission = 1) quyền này không?
     *
     * @return 1 nếu được cấp, null hoặc 0 nếu không.
     */
    @Query(value = """
                SELECT COUNT(*)
                FROM userpermissions up
                JOIN permissions p ON up.permissionID = p.permissionID
                WHERE up.userID = :userId 
                  AND p.permissionname = :permissionCode
                  AND up.activepermission = 1
            """, nativeQuery = true)
    int countUserAllow(Integer userId, String permissionCode);


    /**
     * Check 3: Kiểm tra xem ROLE của User có quyền này không?
     */
    @Query(value = """
                SELECT COUNT(*)
                FROM rolespermissions rp
                JOIN users u ON u.roleID = rp.roleID
                JOIN permissions p ON rp.permissionID = p.permissionID
                WHERE u.userID = :userId 
                  AND p.permissionname = :permissionCode
            """, nativeQuery = true)
    int countRoleAllow(Integer userId, String permissionCode);

    @Query(value = "SELECT is_active FROM permissions WHERE permissionname = :permissionCode", nativeQuery = true)
    Boolean getPermissionActiveStatus(String permissionCode);

    @Query(value = "SELECT permissionID FROM rolespermissions WHERE roleID = :roleId", nativeQuery = true)
    List<Integer> findPermissionIdsByRoleId(Integer roleId);
}
