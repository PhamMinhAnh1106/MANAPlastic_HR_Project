package com.manaplastic.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "employee_documents")
public class EmployeeDocumentEntity {

    public enum DocumentType {
        DEGREE,             // Bằng cấp
        CERTIFICATE,        // Chứng chỉ
        MEDICAL_PREGNANCY,  // Thai sản
        MEDICAL_DISABILITY, // Khuyết tật
        IDENTIFICATION,     // CCCD/Hộ chiếu,...
        OTHER // đủ thứ khacs
    }

    public enum DocumentStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "documentID", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "userID", nullable = false)
    private UserEntity userID;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "documentType", nullable = false)
    private DocumentType documentType;

    @Size(max = 255)
    @NotNull
    @Column(name = "documentName", nullable = false)
    private String documentName;

    @Size(max = 255)
    @Column(name = "issuingAuthority")
    private String issuingAuthority;

    @Column(name = "issueDate")
    private LocalDate issueDate;

    @Column(name = "expiryDate")
    private LocalDate expiryDate;

    @NotNull
    @Column(name = "fileUrl", nullable = false)
    private String fileUrl;

    @ColumnDefault("'PENDING'")
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DocumentStatus status;

    @Lob
    @Column(name = "note")
    private String note;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}