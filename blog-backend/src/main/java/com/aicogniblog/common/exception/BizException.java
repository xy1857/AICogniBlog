package com.aicogniblog.common.exception;

import lombok.Getter;

/**
 * 业务异常类
 * 
 * <p>用于抛出业务逻辑相关的异常，会被全局异常处理器捕获并返回给客户端
 * 
 * @author AICogniBlog Team
 * @since 1.0.0
 */
@Getter
public class BizException extends RuntimeException {

    /** 错误状态码 */
    private final int code;

    /**
     * 构造业务异常（默认状态码 400）
     * 
     * @param message 错误消息
     */
    public BizException(String message) {
        super(message);
        this.code = 400;
    }

    /**
     * 构造业务异常（自定义状态码）
     * 
     * @param code 错误状态码
     * @param message 错误消息
     */
    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }
}
