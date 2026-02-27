package com.aicogniblog.common.util;

import java.util.regex.Pattern;

/**
 * 密码验证工具类
 * 
 * <p>用于验证密码强度和格式
 * 
 * @author AICogniBlog Team
 * @since 1.0.0
 */
public class PasswordValidator {

    /** 密码最小长度 */
    private static final int MIN_LENGTH = 6;
    
    /** 密码最大长度 */
    private static final int MAX_LENGTH = 30;
    
    /** 数字正则 */
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*\\d.*");
    
    /** 字母正则 */
    private static final Pattern LETTER_PATTERN = Pattern.compile(".*[a-zA-Z].*");
    
    /** 特殊字符正则 */
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");

    /**
     * 验证密码格式
     * 
     * @param password 原始密码（解密后）
     * @return 验证结果消息，null 表示验证通过
     */
    public static String validate(String password) {
        if (password == null || password.isEmpty()) {
            return "密码不能为空";
        }
        
        if (password.length() < MIN_LENGTH) {
            return "密码长度不能少于" + MIN_LENGTH + "位";
        }
        
        if (password.length() > MAX_LENGTH) {
            return "密码长度不能超过" + MAX_LENGTH + "位";
        }
        
        // 检查是否包含空格
        if (password.contains(" ")) {
            return "密码不能包含空格";
        }
        
        return null; // 验证通过
    }

    /**
     * 检查密码强度
     * 
     * @param password 原始密码（解密后）
     * @return 强度等级：0-弱，1-中，2-强
     */
    public static int checkStrength(String password) {
        if (password == null || password.length() < MIN_LENGTH) {
            return 0;
        }
        
        int strength = 0;
        
        // 包含数字
        if (DIGIT_PATTERN.matcher(password).matches()) {
            strength++;
        }
        
        // 包含字母
        if (LETTER_PATTERN.matcher(password).matches()) {
            strength++;
        }
        
        // 包含特殊字符
        if (SPECIAL_CHAR_PATTERN.matcher(password).matches()) {
            strength++;
        }
        
        // 长度超过12位额外加分
        if (password.length() >= 12) {
            strength = Math.min(strength + 1, 2);
        }
        
        return Math.min(strength, 2);
    }

    /**
     * 检查密码是否为强密码
     * 
     * @param password 原始密码（解密后）
     * @return true 表示强密码
     */
    public static boolean isStrongPassword(String password) {
        return checkStrength(password) >= 2;
    }
}

