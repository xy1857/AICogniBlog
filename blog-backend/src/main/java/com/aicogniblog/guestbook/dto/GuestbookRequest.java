package com.aicogniblog.guestbook.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 留言请求 DTO
 */
@Data
public class GuestbookRequest {
    
    @NotBlank(message = "昵称不能为空")
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String name;
    
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;
    
    @NotBlank(message = "留言内容不能为空")
    @Size(max = 500, message = "留言内容不能超过500个字符")
    private String content;
}

