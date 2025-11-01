package com.manaplastic.backend.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordDTO {

    @NotBlank(message = "Mật khẩu cũ là bắt buộc")
    private String oldPassword;

    @NotBlank(message = "Mật khẩu mới là bắt buộc")
    private String newPassword;
}
