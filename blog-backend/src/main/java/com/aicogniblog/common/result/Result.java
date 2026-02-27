package com.aicogniblog.common.result;

import lombok.Data;

/**
 * 统一响应结果封装类
 * 
 * <p>用于封装所有 API 接口的返回结果，提供统一的响应格式
 * 
 * @param <T> 响应数据类型
 * @author AICogniBlog Team
 * @since 1.0.0
 */
@Data
public class Result<T> {

    /** 响应状态码（200=成功，其他=失败） */
    private int code;
    
    /** 响应消息 */
    private String message;
    
    /** 响应数据 */
    private T data;

    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功响应（带数据）
     * 
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 成功结果
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    /**
     * 成功响应（带自定义消息和数据）
     * 
     * @param message 响应消息
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 成功结果
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    /**
     * 成功响应（仅消息，无数据）
     * 
     * @param message 响应消息
     * @param <T> 数据类型
     * @return 成功结果
     */
    public static <T> Result<T> success(String message) {
        return new Result<>(200, message, null);
    }

    /**
     * 失败响应（带状态码和消息）
     * 
     * @param code 错误状态码
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 失败结果
     */
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 失败响应（仅消息，状态码默认 500）
     * 
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 失败结果
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }
}
