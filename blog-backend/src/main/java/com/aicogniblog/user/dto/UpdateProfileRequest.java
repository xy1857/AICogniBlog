package com.aicogniblog.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Size(max = 50, message = "昵称最多50个字符")
    private String nickname;

    @Size(max = 200, message = "简介最多200个字符")
    private String bio;

    private String avatarBase64;
}
