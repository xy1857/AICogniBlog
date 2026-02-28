package com.aicogniblog.common.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.security.KeyPair;

/**
 * RSA 密钥对生成器（仅用于开发环境生成密钥对）
 * 生产环境应该使用固定的密钥对
 */
@Component
public class RsaKeyGenerator implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        // 仅在需要时取消注释以生成新的密钥对
        // KeyPair keyPair = RsaUtil.generateKeyPair();
        // System.out.println("=== RSA 密钥对 ===");
        // System.out.println("公钥：");
        // System.out.println(RsaUtil.getPublicKeyString(keyPair.getPublic()));
        // System.out.println("\n私钥：");
        // System.out.println(RsaUtil.getPrivateKeyString(keyPair.getPrivate()));
    }
}




