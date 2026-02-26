package com.aicogniblog;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("应用启动测试")
class AICogniBlogApplicationTest {

    @Test
    @DisplayName("应用上下文加载成功")
    void contextLoads() {
        assertDoesNotThrow(() -> {
            // 测试Spring上下文是否能正常加载
        });
    }
}

