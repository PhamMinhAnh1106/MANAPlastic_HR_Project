package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.ContractEntity;
import com.manaplastic.backend.entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<ContractEntity, Integer>, JpaSpecificationExecutor<ContractEntity> {

    // Đếm số hợp đồng "Xác định thời hạn" mà nhân viên này đã ký (trừ các bản nháp/đã hủy)
    @Query("SELECT COUNT(c) FROM ContractEntity c WHERE c.userID.id = :userId AND c.type = 'FIXED_TERM' AND c.status != 'DRAFT' AND c.status != 'TERMINATED'")
    int countFixedTermContracts(@Param("userId") Integer userId);

    List<ContractEntity> findAll(Specification<ContractEntity> spec);

    @Query("SELECT c FROM ContractEntity c WHERE c.userID.id = :userId ORDER BY c.startdate DESC")
    List<ContractEntity> findAllByUserId(@Param("userId") Integer userId);

    @Query("SELECT c FROM ContractEntity c WHERE c.userID = :userId AND c.status = 'ACTIVE'")
    Optional<ContractEntity> findActiveContractByUserId(@Param("userId") UserEntity userId);

    @Query("SELECT c FROM ContractEntity c WHERE c.enddate BETWEEN :today AND :thresholdDate AND c.status = 'ACTIVE' ORDER BY c.enddate ASC")
    List<ContractEntity> findExpiringContracts(@Param("today") LocalDate today, @Param("thresholdDate") LocalDate thresholdDate);

    boolean existsByContractCode(String finalCode);
    List<ContractEntity> findByStatusAndEnddateBefore(String status, LocalDate date);

    // Tìm các HĐ đang "CHỜ KÍCH HOẠT" (SIGNED) mà ngày bắt đầu là hôm nay (để chuyển lên ACTIVE)
    List<ContractEntity> findByStatusAndStartdateLessThanEqual(String status, LocalDate date);

    // Hàm check trùng thời gian (Để đảm bảo HĐ mới không đè lên HĐ cũ đang chạy)
    @Query("SELECT COUNT(c) FROM ContractEntity c WHERE c.userID.id = :userId " +
            "AND c.status = 'ACTIVE' " +
            "AND c.id <> :excludeContractId " + // Tránh đếm chính nó khi update
            "AND (:startDate <= c.enddate OR c.enddate IS NULL)")
    int countOverlappingActiveContracts(Integer userId, LocalDate startDate, Integer excludeContractId);

    // Thêm vào trong interface ContractRepository
    @Query("SELECT c FROM ContractEntity c " +
            "WHERE c.userID.id = :userId " +
            "AND c.status = 'ACTIVE' " +
            "AND :date BETWEEN c.startdate AND c.enddate")
    ContractEntity findActiveContract(@Param("userId") Integer userId, @Param("date") LocalDate date);

    // KIẾN THỨC LƯU Ý về Spring để tránh truy vấn db nhiều lần cho 1 tác vụ nhỏ
//    Khi nào truyền Entity? Khi ta đã có sẵn đối tượng User đầy đủ trong tay (ví dụ lúc save contract mới).
//    Khi nào truyền ID (@Param)? Khi ta chỉ muốn viết câu truy vấn nhanh, kiểm tra đếm số lượng, hoặc khi tên biến trong Entity dễ gây hiểu nhầm cho Spring.
}