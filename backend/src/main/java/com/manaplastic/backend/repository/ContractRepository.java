package com.manaplastic.backend.repository;

import com.manaplastic.backend.entity.ContractEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<ContractEntity, Integer> {

    // Đếm số hợp đồng "Xác định thời hạn" mà nhân viên này đã ký (trừ các bản nháp/đã hủy)
    @Query("SELECT COUNT(c) FROM ContractEntity c WHERE c.userID.id = :userId AND c.type = 'FIXED_TERM' AND c.status != 'DRAFT' AND c.status != 'TERMINATED'")
    int countFixedTermContracts(@Param("userId") Integer userId);

    // Sử dụng @Query để tránh lỗi tự động map tên hàm
    @Query("SELECT c FROM ContractEntity c WHERE c.userID.id = :userId AND c.status = :status")
    Optional<ContractEntity> findByUserIdAndStatus(@Param("userId") Integer userId, @Param("status") String status);

    List<ContractEntity> findAll(Specification<ContractEntity> spec);

    @Query("SELECT c FROM ContractEntity c WHERE c.userID.id = :userId ORDER BY c.startdate DESC")
    List<ContractEntity> findAllByUserId(@Param("userId") Integer userId);


    // KIẾN THỨC LƯU Ý về Spring để tránh truy vấn db nhiều lần cho 1 tác vụ nhỏ
//    Khi nào truyền Entity? Khi ta đã có sẵn đối tượng User đầy đủ trong tay (ví dụ lúc save contract mới).
//    Khi nào truyền ID (@Param)? Khi ta chỉ muốn viết câu truy vấn nhanh, kiểm tra đếm số lượng, hoặc khi tên biến trong Entity dễ gây hiểu nhầm cho Spring.
}