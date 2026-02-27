package com.aicogniblog.auth.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 登录失败限制服务
 * 
 * <p>防止暴力破解，限制登录失败次数
 * 
 * @author AICogniBlog Team
 * @since 1.0.0
 */
@Service
public class LoginAttemptService {

    /** 最大失败次数 */
    private static final int MAX_ATTEMPTS = 5;
    
    /** 锁定时间（毫秒）：15分钟 */
    private static final long LOCK_TIME = 15 * 60 * 1000;
    
    /** 存储登录失败次数 */
    private final Map<String, AtomicInteger> attemptsCache = new ConcurrentHashMap<>();
    
    /** 存储锁定时间 */
    private final Map<String, Long> lockTimeCache = new ConcurrentHashMap<>();

    /**
     * 记录登录失败
     * 
     * @param username 用户名
     */
    public void loginFailed(String username) {
        AtomicInteger attempts = attemptsCache.computeIfAbsent(username, k -> new AtomicInteger(0));
        int failCount = attempts.incrementAndGet();
        
        if (failCount >= MAX_ATTEMPTS) {
            lockTimeCache.put(username, System.currentTimeMillis() + LOCK_TIME);
        }
    }

    /**
     * 登录成功，清除失败记录
     * 
     * @param username 用户名
     */
    public void loginSucceeded(String username) {
        attemptsCache.remove(username);
        lockTimeCache.remove(username);
    }

    /**
     * 检查用户是否被锁定
     * 
     * @param username 用户名
     * @return true 表示被锁定
     */
    public boolean isBlocked(String username) {
        Long lockTime = lockTimeCache.get(username);
        if (lockTime == null) {
            return false;
        }
        
        // 检查锁定时间是否已过
        if (System.currentTimeMillis() > lockTime) {
            // 解锁
            attemptsCache.remove(username);
            lockTimeCache.remove(username);
            return false;
        }
        
        return true;
    }

    /**
     * 获取剩余锁定时间（分钟）
     * 
     * @param username 用户名
     * @return 剩余锁定时间（分钟），0 表示未锁定
     */
    public long getRemainingLockTime(String username) {
        Long lockTime = lockTimeCache.get(username);
        if (lockTime == null) {
            return 0;
        }
        
        long remaining = lockTime - System.currentTimeMillis();
        return remaining > 0 ? (remaining / 1000 / 60) : 0;
    }

    /**
     * 获取剩余尝试次数
     * 
     * @param username 用户名
     * @return 剩余尝试次数
     */
    public int getRemainingAttempts(String username) {
        AtomicInteger attempts = attemptsCache.get(username);
        if (attempts == null) {
            return MAX_ATTEMPTS;
        }
        return Math.max(0, MAX_ATTEMPTS - attempts.get());
    }
}

