package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.AddLeaverequestDTO;
import com.manaplastic.backend.DTO.LeaveRequestFilterCriteria;
import com.manaplastic.backend.DTO.LeaverequestDTO;
import com.manaplastic.backend.entity.LeaverequestEntity;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.filters.LeaveRequestFilter;
import com.manaplastic.backend.repository.LeaveRequestRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaverequestService {
    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private EmailService emailService;


    //Tạo đơn - đăng ký đơn
    @Transactional
    public LeaverequestDTO createLeaveRequest(AddLeaverequestDTO dto, UserEntity currentUserId) {
        LeaverequestEntity newRequest = new LeaverequestEntity();

        newRequest.setLeavetype(dto.leavetype());
        newRequest.setStartdate(dto.startdate());
        newRequest.setEnddate(dto.enddate());
        newRequest.setReason(dto.reason());


        newRequest.setUserID(currentUserId);
        newRequest.setRequestdate(LocalDate.now());
        newRequest.setStatus(LeaverequestEntity.LeaverequestStatus.PENDING);

        LeaverequestEntity savedRequest = leaveRequestRepository.save(newRequest);

        return mapToDTO(savedRequest);
    }
    private LeaverequestDTO mapToDTO(LeaverequestEntity entity) {
        return new LeaverequestDTO(
                entity.getId(),
                entity.getUserID().getUsername(),
                entity.getUserID().getFullname(),
                entity.getLeavetype(),
                entity.getStartdate(),
                entity.getEnddate(),
                entity.getReason(),
                entity.getStatus().name(),
                entity.getRequestdate()
        );
    }

    // Lấy danh sách đơn - xem đơn cảu tôi
    public List<LeaverequestDTO> getMyLeaveRequests(UserEntity currentUserId) {
        List<LeaverequestEntity> requests = leaveRequestRepository.findByUserIDOrderByRequestdateDesc(currentUserId);

        return requests.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // xóa đơn (PENDING)
    @Transactional
    public void deleteMyLeaveRequest(Integer leaveRequestId, UserEntity currentUserId) {
        int rowsAffected = leaveRequestRepository.deleteByIdAndUserIDAndStatus(
                leaveRequestId,
                currentUserId,
                LeaverequestEntity.LeaverequestStatus.PENDING
        );

        if (rowsAffected == 0) {
            throw new RuntimeException("Không thể xóa đơn. Đơn không tồn tại, không thuộc về bạn, hoặc đã được xử lý.");
        }
    }

    // Danh sách đã lọc
    public List<LeaverequestDTO> getFilteredRequests(LeaveRequestFilterCriteria criteria) {

        Specification<LeaverequestEntity> spec = LeaveRequestFilter.withCriteria(criteria);
        List<LeaverequestEntity> requests = leaveRequestRepository.findAll(spec);

        return requests.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Duyệt đơn
    @Transactional
    public void approveRequest(Integer leaveRequestId) {
        LeaverequestEntity request = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn nghỉ phép."));

        if (request.getStatus() != LeaverequestEntity.LeaverequestStatus.PENDING) {
            throw new RuntimeException("Đơn này đã được xử lý, không thể duyệt.");
        }

        request.setStatus(LeaverequestEntity.LeaverequestStatus.APPROVED);
        LeaverequestEntity savedRequest = leaveRequestRepository.save(request);

        String email = savedRequest.getUserID().getEmail();
        String fullname = savedRequest.getUserID().getFullname();

        emailService.sendApprovalEmail(email, fullname, savedRequest);
    }

    // Từ chối đơn
    @Transactional
    public void rejectRequest(Integer leaveRequestId) {
        LeaverequestEntity request = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn nghỉ phép."));

        if (request.getStatus() != LeaverequestEntity.LeaverequestStatus.PENDING) { // tránh tình trạng đã duyệt rô mà đi từ chối lại
            throw new RuntimeException("Đơn này đã được xử lý, không thể từ chối.");
        }

        request.setStatus(LeaverequestEntity.LeaverequestStatus.REJECTED);
        LeaverequestEntity savedRequest = leaveRequestRepository.save(request);

        String email = savedRequest.getUserID().getEmail();
        String fullname = savedRequest.getUserID().getFullname();

        emailService.sendRejectionEmail(email, fullname, savedRequest);
    }

}
